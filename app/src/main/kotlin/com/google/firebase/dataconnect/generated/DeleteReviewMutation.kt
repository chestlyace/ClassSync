
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



public interface DeleteReviewMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      DeleteReviewMutation.Data,
      DeleteReviewMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val movieId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val review_delete: ReviewKey?
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteReview"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteReviewMutation.ref(
  
    movieId: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteReviewMutation.Data,
    DeleteReviewMutation.Variables
  > =
  ref(
    
      DeleteReviewMutation.Variables(
        movieId=movieId,
  
      )
    
  )

public suspend fun DeleteReviewMutation.execute(

  
    
      movieId: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteReviewMutation.Data,
    DeleteReviewMutation.Variables
  > =
  ref(
    
      movieId=movieId,
  
    
  ).execute()


