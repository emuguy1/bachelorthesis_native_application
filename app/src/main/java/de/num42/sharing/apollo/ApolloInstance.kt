package de.num42.sharing.apollo

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApolloInstance {
    var BASE_URL = "http://192.168.188.69:4000/graphql/graphiql/"

    private val httpClient : OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .callTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor).build()
    }

    private var client = create()


    fun get(): ApolloClient{
        return client
    }

    private fun create(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .okHttpClient(httpClient)
            .build()
    }

    fun setAuthorization(authToken: String):ApolloClient{
        if(!BASE_URL.contains("?")){
            BASE_URL += "?authorization="+authToken+"&"
        }
        client = create()
        return client
    }

    fun resetAuthorization():ApolloClient{
        if(BASE_URL.contains("?")){
            BASE_URL = BASE_URL.substringBefore("?")
        }
        client = create()
        return client
    }
}