
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



public interface UpsertUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      UpsertUserMutation.Data,
      UpsertUserMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val username: String
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user_upsert: UserKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpsertUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpsertUserMutation.ref(
  
    username: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      UpsertUserMutation.Variables(
        username=username,
  
      )
    
  )

public suspend fun UpsertUserMutation.execute(

  
    
      username: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      username=username,
  
    
  ).execute()


