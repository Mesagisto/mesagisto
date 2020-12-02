package io.github.itsusinn.mc.easyforward.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File

class ImageUploadService(di: DI) {
   private val client by lazy { OkHttpClient() }
   private val configService: ConfigService by di.instance()
   private val token = configService.config.smms

   suspend fun upload(file:File):String?{
      val result:String

         try {
            withContext(Dispatchers.IO){
               val request = build(file)
               val response = client.newCall(request).execute()
               val raw = response.body?.string()
                  ?: error("Wrong SMMS")
               val start = raw.indexOf("\"url\":\"")+7
               val end = raw.indexOf("\",\"",start)
               result = raw.substring(start,end).replace("\\/","/")
            }
         }catch (e:Exception){
            return null
         }
      if (!result.startsWith("https:")) return null
      return result
   }

   private fun build(file: File):Request{
      val fileBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

      val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
         .addFormDataPart("smfile", file.name, fileBody).build()

      return Request.Builder()
         .url("https://sm.ms/api/v2/upload")
         .addHeader("Authorization", token)
         .addHeader("User-Agent", "Minecraft-Bukkit")
         .post(requestBody)
         .build()
   }
}