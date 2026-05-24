
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


public interface ListUserReviewsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      ListUserReviewsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user: User?
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class User(
  
    val id: String,
    val username: String,
    val reviews: List<ReviewsItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ReviewsItem(
  
    val rating: Int?,
    val reviewDate: com.google.firebase.dataconnect.LocalDate,
    val reviewText: String?,
    val movie: Movie
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Movie(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListUserReviews"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListUserReviewsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListUserReviewsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListUserReviewsQuery.execute(

  
    fetchPolicy: com.google.firebase.dataconnect.QueryRef.FetchPolicy = com.google.firebase.dataconnect.QueryRef.FetchPolicy.PREFER_CACHE,
  

  ): com.google.firebase.dataconnect.QueryResult<
    ListUserReviewsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute(fetchPolicy = fetchPolicy)


  public fun ListUserReviewsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListUserReviewsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

