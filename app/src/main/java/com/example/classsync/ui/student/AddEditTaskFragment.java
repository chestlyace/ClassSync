package com.example.classsync.ui.student;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.cache.DataCache;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.firebase.GroupRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class AddEditTaskFragment extends Fragment {

    private String courseId;
    private String assignmentId;
    private String groupId;

    private GroupRepository groupRepository;
    private UserSession userSession;
    private FirebaseFirestore db;
    private DataCache dataCache;

    private EditText inputTaskName;
    private EditText inputNotes;
    private LinearLayout membersContainer;
    private LinearLayout datePickerRow;
    private TextView selectedDateText;
    private Button btnPickDate;
    private SwitchCompat switchDeadline;

    private String selectedMemberUid;
    private Date selectedDeadline;
    private ListenerRegistration groupDocListener;

    private static final int[] AVATAR_COLORS = {
            0xFFD8E2FF, 0xFFE8DEF8, 0xFFFFF3D8, 0xFFFAD8FD,
            0xFFD8F8E8, 0xFFFFE0D8
    };
    private static final int[] AVATAR_TEXT_COLORS = {
            0xFF1A4FC4, 0xFF6750A4, 0xFF8D6E00, 0xFFB3261E,
            0xFF1C7D4A, 0xFFB34A1A
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId", "");
            assignmentId = getArguments().getString("assignmentId", "");
            groupId = getArguments().getString("groupId", "");
        }
        groupRepository = new GroupRepository();
        userSession = new UserSession(requireContext());
        db = FirebaseFirestore.getInstance();
        dataCache = DataCache.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputTaskName = view.findViewById(R.id.input_task_name);
        inputNotes = view.findViewById(R.id.input_notes);
        membersContainer = view.findViewById(R.id.members_container);
        datePickerRow = view.findViewById(R.id.date_picker_row);
        selectedDateText = view.findViewById(R.id.selected_date_text);
        btnPickDate = view.findViewById(R.id.btn_pick_date);
        switchDeadline = view.findViewById(R.id.switch_deadline);

        // Default: assign to self
        selectedMemberUid = userSession.getUserUid();

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

        view.findViewById(R.id.btn_save_task).setOnClickListener(v -> saveTask());

        view.findViewById(R.id.btn_delete_task).setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        switchDeadline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            datePickerRow.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked && selectedDeadline == null) {
                selectedDeadline = new Date();
                updateDateText();
            }
        });

        btnPickDate.setOnClickListener(v -> showDatePicker());

        // Set default deadline to tomorrow
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        selectedDeadline = cal.getTime();
        updateDateText();

        listenForGroupDoc();
    }

    private void listenForGroupDoc() {
        String docPath = FirestorePaths.groupDocument(courseId, assignmentId, groupId);

        // Populate from cache immediately
        DocumentSnapshot cached = dataCache.getDocument(docPath);
        if (cached != null && cached.exists()) {
            populateMembersFromSnapshot(cached);
        }

        groupDocListener = db.document(docPath)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;
                    dataCache.putDocument(docPath, snapshot);
                    populateMembersFromSnapshot(snapshot);
                });
    }

    @SuppressWarnings("unchecked")
    private void populateMembersFromSnapshot(@NonNull DocumentSnapshot snapshot) {
        Map<String, String> memberNames = (Map<String, String>) snapshot.get("memberNames");
        if (memberNames == null) return;

        populateMemberChips(memberNames);
    }

    private void populateMemberChips(@NonNull Map<String, String> memberNames) {
        membersContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int index = 0;

        for (Map.Entry<String, String> entry : memberNames.entrySet()) {
            String uid = entry.getKey();
            String name = entry.getValue();

            View chip = inflater.inflate(R.layout.chip_member, membersContainer, false);

            TextView initialText = chip.findViewById(R.id.chip_avatar_initial);
            View bg = chip.findViewById(R.id.chip_avatar_bg);
            TextView nameText = chip.findViewById(R.id.chip_member_name);
            FrameLayout checkmark = chip.findViewById(R.id.chip_checkmark);

            int colorIndex = index % AVATAR_COLORS.length;
            bg.setBackgroundTintList(android.content.res.ColorStateList.valueOf(AVATAR_COLORS[colorIndex]));
            initialText.setText(String.valueOf(name.charAt(0)).toUpperCase());
            initialText.setTextColor(AVATAR_TEXT_COLORS[colorIndex]);
            nameText.setText(name);

            // Highlight if selected
            if (uid.equals(selectedMemberUid)) {
                checkmark.setVisibility(View.VISIBLE);
                nameText.setTextColor(0xFF1A4FC4);
                nameText.setTypeface(null, android.graphics.Typeface.BOLD);
            }

            chip.setOnClickListener(v -> selectMember(uid, memberNames));
            membersContainer.addView(chip);
            index++;
        }
    }

    private void selectMember(String uid, Map<String, String> memberNames) {
        selectedMemberUid = uid;
        populateMemberChips(memberNames);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDeadline != null ? selectedDeadline : new Date());

        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth);
                    selectedDeadline = picked.getTime();
                    updateDateText();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        picker.getDatePicker().setMinDate(System.currentTimeMillis());
        picker.show();
    }

    private void updateDateText() {
        if (selectedDeadline != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, EEE", Locale.US);
            selectedDateText.setText(sdf.format(selectedDeadline));
        }
    }

    private void saveTask() {
        String title = inputTaskName.getText().toString().trim();
        if (title.isEmpty()) {
            inputTaskName.setError("Task name is required");
            return;
        }

        String notes = inputNotes.getText().toString().trim();
        String name = userSession.getUserName();

        groupRepository.addTask(courseId, assignmentId, groupId,
                title, selectedMemberUid, name,
                notes.isEmpty() ? null : notes,
                selectedDeadline);

        // Invalidate task cache so workspace refreshes
        dataCache.invalidatePrefix(FirestorePaths.tasksCollection(courseId, assignmentId, groupId));

        Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onDestroyView() {
        if (groupDocListener != null) groupDocListener.remove();
        super.onDestroyView();
    }
}
