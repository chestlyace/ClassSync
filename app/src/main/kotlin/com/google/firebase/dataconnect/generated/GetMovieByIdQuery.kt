
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetMovieByIdQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetMovieByIdQuery.Data,
      GetMovieByIdQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val movie: Movie?
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Movie(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val imageUrl: String,
    val genre: String?,
    val metadata: Metadata?,
    val reviews: List<ReviewsItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Metadata(
  
    val rating: Double?,
    val releaseYear: Int?,
    val description: String?
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ReviewsItem(
  
    val reviewText: String?,
    val reviewDate: com.google.firebase.dataconnect.LocalDate,
    val rating: Int?,
    val user: User
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class User(
  
    val id: String,
    val username: String
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetMovieById"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetMovieByIdQuery.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetMovieByIdQuery.Data,
    GetMovieByIdQuery.Variables
  > =
  ref(
    
      GetMovieByIdQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetMovieByIdQuery.execute(

  
    
      id: java.util.UUID,
  fetchPolicy: com.google.firebase.dataconnect.QueryRef.FetchPolicy = com.google.firebase.dataconnect.QueryRef.FetchPolicy.PREFER_CACHE,
  

  ): com.google.firebase.dataconnect.QueryResult<
    GetMovieByIdQuery.Data,
    GetMovieByIdQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute(fetchPolicy = fetchPolicy)


  public fun GetMovieByIdQuery.flow(
    
      id: java.util.UUID,

  
    
    ): kotlinx.coroutines.flow.Flow<GetMovieByIdQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

