package com.example.rick_morty

data class CharacterResponse(
    val results: List<RemoteCharacter>
)

data class RemoteCharacter(
    val id: Int,
    val name: String,
    val species: String,
    val status: String,
    val image: String
)

fun RemoteCharacter.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        species = this.species,
        imageUrl = this.image
    )
}

