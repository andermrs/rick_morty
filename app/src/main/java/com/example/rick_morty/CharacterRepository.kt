package com.example.rick_morty

import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val characterDao: CharacterDao,
    private val api: RickAndMortyApi
) {
    val allCharacters: Flow<List<CharacterEntity>> = characterDao.getAllCharacters()

    suspend fun syncCharacters() {
        val response = api.getCharacters()
        val entities = response.results.map { it.toEntity() }
        characterDao.insertCharacters(entities)
    }
}