---
name: Academic Synergy
colors:
  surface: '#f9f9ff'
  surface-dim: '#d3daef'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f3ff'
  surface-container: '#e9edff'
  surface-container-high: '#e1e8fd'
  surface-container-highest: '#dce2f7'
  on-surface: '#141b2b'
  on-surface-variant: '#434654'
  inverse-surface: '#293040'
  inverse-on-surface: '#edf0ff'
  outline: '#737686'
  outline-variant: '#c3c5d7'
  surface-tint: '#1353d8'
  primary: '#003fb1'
  on-primary: '#ffffff'
  primary-container: '#1a56db'
  on-primary-container: '#d4dcff'
  inverse-primary: '#b5c4ff'
  secondary: '#006c53'
  on-secondary: '#ffffff'
  secondary-container: '#89f7d1'
  on-secondary-container: '#007258'
  tertiary: '#694100'
  on-tertiary: '#ffffff'
  tertiary-container: '#895600'
  on-tertiary-container: '#ffd6a8'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b5c4ff'
  on-primary-fixed: '#00174d'
  on-primary-fixed-variant: '#003dab'
  secondary-fixed: '#89f7d1'
  secondary-fixed-dim: '#6cdab6'
  on-secondary-fixed: '#002117'
  on-secondary-fixed-variant: '#00513e'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#f9f9ff'
  on-background: '#141b2b'
  surface-variant: '#dce2f7'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 28px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '700'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-page: 16px
  gutter-stack: 12px
  padding-card: 16px
  touch-target-height: 52px
  icon-size-md: 24px
---

## Brand & Style

The design system is built for an academic environment where clarity, efficiency, and reliability are paramount. It follows a **Modern Flat** aesthetic, eschewing heavy shadows and complex gradients in favor of structural integrity and color-coded information hierarchy. 

The personality is "Professional Academic"—it feels like a focused workspace rather than a social media app. It utilizes high-contrast typography and a rigid grid to ensure students and teachers can navigate complex schedules and assignments without cognitive fatigue. The visual language is defined by clean lines, generous whitespace, and a card-based architecture that encapsulates data into digestible units.

## Colors

The color palette is functional and semantic, designed to distinguish between user roles and urgency levels at a glance:

- **Primary (Deep Blue):** Reserved for student-facing actions, navigation, and primary brand touchpoints.
- **Secondary (Teal Green):** Specifically for educator-related tools, grading, and teacher-led activities.
- **Accent (Amber):** Used sparingly for upcoming deadlines, warnings, or items requiring immediate attention.
- **Danger (Red):** Exclusively for overdue assignments or critical errors.
- **Surface Strategy:** The UI uses an "Off-white" (#F8F9FA) base for the screen background to reduce eye strain, while "Pure White" (#FFFFFF) is used for cards and interactive surfaces to create a clear "layer" effect without needing shadows.

## Typography

This design system utilizes **Inter** for its systematic, neutral, and highly legible characteristics. 

- **Headlines:** Use Bold (700) weight to anchor the page. For mobile screens, headlines are kept compact to ensure maximum content visibility.
- **Body:** Use Regular (400) weight for all descriptive text. Ensure a minimum of 1.4x line-height for readability during long reading sessions.
- **Labels/Captions:** Use Medium (500) weight for buttons, metadata, and micro-copy. This provides a clear visual distinction from standard body text without requiring a change in size.

## Layout & Spacing

The layout is built on a strict mobile-first grid focused on vertical stacking. 

- **Horizontal Margins:** A consistent 16px margin is applied to the left and right of the screen.
- **Vertical Rhythm:** Components are spaced using a 4px baseline. Cards typically have a 12px or 16px vertical gap between them to maintain a "list" feel while keeping related information grouped.
- **Touch Targets:** All primary interactive elements (Buttons, Inputs) are normalized to a 52px height to ensure accessibility and ease of use in high-stress academic environments.

## Elevation & Depth

This design system adopts a **Flat Tonal** approach to depth. No drop shadows are used to represent elevation. Instead:

- **Borders:** All cards and interactive containers use a 1px solid border (#E5E7EB) to define their boundaries against the off-white background.
- **Z-Index via Contrast:** Elements that appear "on top" (like Bottom Sheets or Navigation Bars) are distinguished by their pure white fill and the crispness of their top-border dividers.
- **Active States:** Subtle tonal shifts (e.g., a 5% darker tint of the primary color) indicate press states rather than lifting the element off the page.

## Shapes

The shape language is varied to create a clear functional hierarchy:

- **Cards:** 12px radius. This is the primary container for information, providing a friendly, approachable corner that still feels structured.
- **Inputs & Buttons:** 8px radius. A slightly sharper corner helps these functional elements stand out from informational cards, signaling interactivity.
- **Pills/Chips:** 24px radius. Used for status indicators (e.g., "In Progress," "Submitted") or category tags to provide a distinct visual contrast from the rectangular layout.

## Components

### Buttons & Inputs
- **Height:** Fixed at 52px.
- **Buttons:** Filled with Primary (#1A56DB) for students or Secondary (#0D9373) for teachers. Label-md font weight.
- **Inputs:** White background, 1px #E5E7EB border, 8px radius. Text-secondary for placeholders.

### Cards
- **Construction:** 1px border (#E5E7EB), White background, 12px radius.
- **Content:** Headline-sm for titles, Body-md for descriptions, and Label-sm for metadata (e.g., date, subject).

### Navigation
- **Bottom Bar:** 56px - 64px height, pure white background with a 1px top border. Use active tints of Primary Blue for the selected icon and label.
- **Tabs:** Underline style for active states using a 2px stroke of the primary color.

### Feedback & State
- **Skeleton Loaders:** Use a light grey (#F3F4F6) base with a subtle linear shimmer. Maintain the shape radii of the components being replaced.
- **Empty States:** Centered layout using flat, 2D illustrations with a desaturated version of the primary palette. Headline-md for the title and Body-md for instructions.
- **Back Arrows:** 24px icons with a 16px touch padding, placed in a consistent 56px tall top header.