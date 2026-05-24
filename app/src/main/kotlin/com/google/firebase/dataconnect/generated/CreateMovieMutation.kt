
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



public interface CreateMovieMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CreateMovieMutation.Data,
      CreateMovieMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val title: String,
    val genre: String,
    val imageUrl: String
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val movie_insert: MovieKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateMovie"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateMovieMutation.ref(
  
    title: String,genre: String,imageUrl: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CreateMovieMutation.Data,
    CreateMovieMutation.Variables
  > =
  ref(
    
      CreateMovieMutation.Variables(
        title=title,genre=genre,imageUrl=imageUrl,
  
      )
    
  )

public suspend fun CreateMovieMutation.execute(

  
    
      title: String,genre: String,imageUrl: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CreateMovieMutation.Data,
    CreateMovieMutation.Variables
  > =
  ref(
    
      title=title,genre=genre,imageUrl=imageUrl,
  
    
  ).execute()


