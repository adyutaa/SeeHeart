import com.marshielo.seeheart.data.api.FoodApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.api-ninjas.com/v1/nutrition"
    private const val API_KEY = "XZP7N1/ZViHppFTthD9WHA==BsKzvUGHVpkmNYl2"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Api-Key", API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val foodApiService: FoodApiService by lazy {
        retrofit.create(FoodApiService::class.java)
    }
}
