
@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "LocalVariableName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "LocalVariableName",
  "unused",
)

package com.google.firebase.dataconnect.generated

import com.google.firebase.dataconnect.getInstance as _fdcGetInstance
import kotlin.time.Duration.Companion.milliseconds as _milliseconds

public interface ExampleConnector : com.google.firebase.dataconnect.generated.GeneratedConnector<ExampleConnector> {
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect

  
    public val addReview: AddReviewMutation
  
    public val createMovie: CreateMovieMutation
  
    public val deleteReview: DeleteReviewMutation
  
    public val getMovieById: GetMovieByIdQuery
  
    public val listMovies: ListMoviesQuery
  
    public val listUserReviews: ListUserReviewsQuery
  
    public val listUsers: ListUsersQuery
  
    public val searchMovie: SearchMovieQuery
  
    public val upsertUser: UpsertUserMutation
  

  public companion object {
    @Suppress("MemberVisibilityCanBePrivate")
    public val config: com.google.firebase.dataconnect.ConnectorConfig = com.google.firebase.dataconnect.ConnectorConfig(
      connector = "example",
      location = "us-east4",
      serviceId = "classsync",
    )

    public fun getInstance(
      dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
    ):ExampleConnector = synchronized(instances) {
      instances.getOrPut(dataConnect) {
        ExampleConnectorImpl(dataConnect)
      }
    }

    private val instances = java.util.WeakHashMap<com.google.firebase.dataconnect.FirebaseDataConnect, ExampleConnectorImpl>()

    
    public val defaultCacheSettings: com.google.firebase.dataconnect.CacheSettings =
      com.google.firebase.dataconnect.CacheSettings(
        
        
      )

    public val defaultDataConnectSettings: com.google.firebase.dataconnect.DataConnectSettings =
      com.google.firebase.dataconnect.DataConnectSettings(
        cacheSettings = defaultCacheSettings,
      )
    
  }
}

public val ExampleConnector.Companion.instance:ExampleConnector
  get() = getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(
    config, defaultDataConnectSettings
  ))

public fun ExampleConnector.Companion.getInstance(
  settings: com.google.firebase.dataconnect.DataConnectSettings = defaultDataConnectSettings
):ExampleConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(config, settings))

public fun ExampleConnector.Companion.getInstance(
  app: com.google.firebase.FirebaseApp,
  settings: com.google.firebase.dataconnect.DataConnectSettings = defaultDataConnectSettings
):ExampleConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(app, config, settings))

private class ExampleConnectorImpl(
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
) : ExampleConnector {
  
    override val addReview by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddReviewMutationImpl(this)
    }
  
    override val createMovie by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateMovieMutationImpl(this)
    }
  
    override val deleteReview by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteReviewMutationImpl(this)
    }
  
    override val getMovieById by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMovieByIdQueryImpl(this)
    }
  
    override val listMovies by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListMoviesQueryImpl(this)
    }
  
    override val listUserReviews by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListUserReviewsQueryImpl(this)
    }
  
    override val listUsers by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListUsersQueryImpl(this)
    }
  
    override val searchMovie by lazy(LazyThreadSafetyMode.PUBLICATION) {
      SearchMovieQueryImpl(this)
    }
  
    override val upsertUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpsertUserMutationImpl(this)
    }
  

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun operations(): List<com.google.firebase.dataconnect.generated.GeneratedOperation<ExampleConnector, *, *>> =
    queries() + mutations()

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun mutations(): List<com.google.firebase.dataconnect.generated.GeneratedMutation<ExampleConnector, *, *>> =
    listOf(
      addReview,
        createMovie,
        deleteReview,
        upsertUser,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<ExampleConnector, *, *>> =
    listOf(
      getMovieById,
        listMovies,
        listUserReviews,
        listUsers,
        searchMovie,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect) =
    ExampleConnectorImpl(dataConnect)

  override fun equals(other: Any?): Boolean =
    other is ExampleConnectorImpl &&
    other.dataConnect == dataConnect

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "ExampleConnectorImpl",
      dataConnect,
    )

  override fun toString(): String =
    "ExampleConnectorImpl(dataConnect=$dataConnect)"
}



private open class ExampleConnectorGeneratedQueryImpl<Data, Variables>(
  override val connector: ExampleConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedQuery<ExampleConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: ExampleConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    ExampleConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    ExampleConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    ExampleConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is ExampleConnectorGeneratedQueryImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "ExampleConnectorGeneratedQueryImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "ExampleConnectorGeneratedQueryImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}

private open class ExampleConnectorGeneratedMutationImpl<Data, Variables>(
  override val connector: ExampleConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedMutation<ExampleConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: ExampleConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    ExampleConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    ExampleConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    ExampleConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is ExampleConnectorGeneratedMutationImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "ExampleConnectorGeneratedMutationImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "ExampleConnectorGeneratedMutationImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}



private class AddReviewMutationImpl(
  connector: ExampleConnector
):
  AddReviewMutation,
  ExampleConnectorGeneratedMutationImpl<
      AddReviewMutation.Data,
      AddReviewMutation.Variables
  >(
    connector,
    AddReviewMutation.Companion.operationName,
    AddReviewMutation.Companion.dataDeserializer,
    AddReviewMutation.Companion.variablesSerializer,
  )


private class CreateMovieMutationImpl(
  connector: ExampleConnector
):
  CreateMovieMutation,
  ExampleConnectorGeneratedMutationImpl<
      CreateMovieMutation.Data,
      CreateMovieMutation.Variables
  >(
    connector,
    CreateMovieMutation.Companion.operationName,
    CreateMovieMutation.Companion.dataDeserializer,
    CreateMovieMutation.Companion.variablesSerializer,
  )


private class DeleteReviewMutationImpl(
  connector: ExampleConnector
):
  DeleteReviewMutation,
  ExampleConnectorGeneratedMutationImpl<
      DeleteReviewMutation.Data,
      DeleteReviewMutation.Variables
  >(
    connector,
    DeleteReviewMutation.Companion.operationName,
    DeleteReviewMutation.Companion.dataDeserializer,
    DeleteReviewMutation.Companion.variablesSerializer,
  )


private class GetMovieByIdQueryImpl(
  connector: ExampleConnector
):
  GetMovieByIdQuery,
  ExampleConnectorGeneratedQueryImpl<
      GetMovieByIdQuery.Data,
      GetMovieByIdQuery.Variables
  >(
    connector,
    GetMovieByIdQuery.Companion.operationName,
    GetMovieByIdQuery.Companion.dataDeserializer,
    GetMovieByIdQuery.Companion.variablesSerializer,
  )


private class ListMoviesQueryImpl(
  connector: ExampleConnector
):
  ListMoviesQuery,
  ExampleConnectorGeneratedQueryImpl<
      ListMoviesQuery.Data,
      Unit
  >(
    connector,
    ListMoviesQuery.Companion.operationName,
    ListMoviesQuery.Companion.dataDeserializer,
    ListMoviesQuery.Companion.variablesSerializer,
  )


private class ListUserReviewsQueryImpl(
  connector: ExampleConnector
):
  ListUserReviewsQuery,
  ExampleConnectorGeneratedQueryImpl<
      ListUserReviewsQuery.Data,
      Unit
  >(
    connector,
    ListUserReviewsQuery.Companion.operationName,
    ListUserReviewsQuery.Companion.dataDeserializer,
    ListUserReviewsQuery.Companion.variablesSerializer,
  )


private class ListUsersQueryImpl(
  connector: ExampleConnector
):
  ListUsersQuery,
  ExampleConnectorGeneratedQueryImpl<
      ListUsersQuery.Data,
      Unit
  >(
    connector,
    ListUsersQuery.Companion.operationName,
    ListUsersQuery.Companion.dataDeserializer,
    ListUsersQuery.Companion.variablesSerializer,
  )


private class SearchMovieQueryImpl(
  connector: ExampleConnector
):
  SearchMovieQuery,
  ExampleConnectorGeneratedQueryImpl<
      SearchMovieQuery.Data,
      SearchMovieQuery.Variables
  >(
    connector,
    SearchMovieQuery.Companion.operationName,
    SearchMovieQuery.Companion.dataDeserializer,
    SearchMovieQuery.Companion.variablesSerializer,
  )


private class UpsertUserMutationImpl(
  connector: ExampleConnector
):
  UpsertUserMutation,
  ExampleConnectorGeneratedMutationImpl<
      UpsertUserMutation.Data,
      UpsertUserMutation.Variables
  >(
    connector,
    UpsertUserMutation.Companion.operationName,
    UpsertUserMutation.Companion.dataDeserializer,
    UpsertUserMutation.Companion.variablesSerializer,
  )


