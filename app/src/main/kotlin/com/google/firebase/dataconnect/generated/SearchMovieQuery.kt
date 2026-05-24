
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


public interface SearchMovieQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      SearchMovieQuery.Data,
      SearchMovieQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val titleInput: com.google.firebase.dataconnect.OptionalVariable<String?>,
    val genre: com.google.firebase.dataconnect.OptionalVariable<String?>
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var titleInput: String?
        public var genre: String?
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          
          block_: Builder.() -> Unit
        ): Variables {
          var titleInput: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var genre: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var titleInput: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { titleInput = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var genre: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { genre = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              titleInput=titleInput,genre=genre,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val movies: List<MoviesItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class MoviesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val genre: String?,
    val imageUrl: String
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "SearchMovie"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SearchMovieQuery.ref(
  
    

  
    block_: SearchMovieQuery.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.QueryRef<
    SearchMovieQuery.Data,
    SearchMovieQuery.Variables
  > =
  ref(
    
      SearchMovieQuery.Variables.build(
        
  
    block_
      )
    
  )

public suspend fun SearchMovieQuery.execute(

  
    
      
  fetchPolicy: com.google.firebase.dataconnect.QueryRef.FetchPolicy = com.google.firebase.dataconnect.QueryRef.FetchPolicy.PREFER_CACHE,
  
    block_: SearchMovieQuery.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.QueryResult<
    SearchMovieQuery.Data,
    SearchMovieQuery.Variables
  > =
  ref(
    
      
  
    block_
    
  ).execute(fetchPolicy = fetchPolicy)


  public fun SearchMovieQuery.flow(
    
      

  
    block_: SearchMovieQuery.Variables.Builder.() -> Unit = {}
    
    ): kotlinx.coroutines.flow.Flow<SearchMovieQuery.Data> =
    ref(
        
          
  
    block_
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

