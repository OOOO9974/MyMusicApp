package com.example.mymusicapp.data

import android.util.Log
import com.example.mymusicapp.data.network.RetrofitInstance
import com.example.mymusicapp.data.network.response.MyItemResponse
import com.example.mymusicapp.data.network.response.MyListResponse
import com.example.mymusicapp.data.network.response.MyResponse
import com.example.mymusicapp.data.network.music.MusicRequest
import com.example.mymusicapp.data.network.music.MusicResponse
import com.example.mymusicapp.data.network.music.MusicResponseFounderItem
import com.example.mymusicapp.models.Music
class MusicRepository {
    suspend fun getMusicList(): List<Music> {
        val music = mutableListOf<Music>()

        try {

            val response: MyListResponse<MusicResponse> =
                RetrofitInstance.musicService.getAllMusic("09974")
            val musicFromResponse = response.data

            if (musicFromResponse != null) {
                for (musicFromResponse in musicFromResponse) {
                    if (musicFromResponse.description != null) {
                        music.add(
                            Music(
                                id = musicFromResponse.id.toString(),
                                title = musicFromResponse.name.uppercase(),
                                description = musicFromResponse.description.toString(),
                                founders = musicFromResponse.founders as List<String>,
                                gender = musicFromResponse.gender,
                                size = musicFromResponse.size,
                                price = musicFromResponse.price
                            )
                        )
                    }
                }

            }
        }   catch (ex: Exception){
            ex.printStackTrace()
        }

        return music
    }

    suspend fun insertNewMusic(music: Music): MyResponse? {
        val response: MyResponse

        try {
            val musicRequest =
                MusicRequest(
                    title = music.title,
                    description = music.description,
                    founders = music.founders,
                    gender = music.gender,
                    price = music.price,
                    size = music.size
                )

            response = RetrofitInstance.musicService.insertNewMusic(
                "09974",
                musicRequest
            )

            Log.d("Update_response", response.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return response
    }

    suspend fun getMusicById(musicId: String): Music? {
        try {
            val response: MyItemResponse<MusicResponse> =
                RetrofitInstance.musicService.getOneMusicById(musicId, "09974")
            val musicFromResponse = response.data

            if (musicFromResponse != null) {
                if (musicFromResponse.description != null
                ) {
                    return Music(
                        id = musicId,
                        title = musicFromResponse.name,
                        description = musicFromResponse.description,
                        founders = extractListOfFoundersFromResponse(musicFromResponse.founders),
                        price = musicFromResponse.price,
                        gender = musicFromResponse.gender,
                        size = musicFromResponse.size
                    )
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }

    private fun extractListOfFoundersFromResponse(
        foundersFromResponse: List<MusicResponseFounderItem>
    ): List<String> {

        val myFounders = mutableListOf<String>()

        for (founderObj in foundersFromResponse) {
            myFounders.add(founderObj.founderName)
        }

        return myFounders
    }
}