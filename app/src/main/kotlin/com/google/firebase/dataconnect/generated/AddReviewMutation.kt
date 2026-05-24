
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



public interface AddReviewMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AddReviewMutation.Data,
      AddReviewMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val movieId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val rating: Int,
    val reviewText: String
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val review_upsert: ReviewKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AddReview"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AddReviewMutation.ref(
  
    movieId: java.util.UUID,rating: Int,reviewText: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AddReviewMutation.Data,
    AddReviewMutation.Variables
  > =
  ref(
    
      AddReviewMutation.Variables(
        movieId=movieId,rating=rating,reviewText=reviewText,
  
      )
    
  )

public suspend fun AddReviewMutation.execute(

  
    
      movieId: java.util.UUID,rating: Int,reviewText: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AddReviewMutation.Data,
    AddReviewMutation.Variables
  > =
  ref(
    
      movieId=movieId,rating=rating,reviewText=reviewText,
  
    
  ).execute()


