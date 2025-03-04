package pro.branium.messenger.utils

object CloudFunctionUrlBuilder {
    private const val BASE_DOMAIN = "otvgwuq6eq-uc.a.run.app"

    fun buildBaseUrl(functionName: String): String {
        return "https://$functionName-$BASE_DOMAIN"
    }
}