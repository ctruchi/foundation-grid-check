package fr.ctruchi.foundationGridCheck

data class FileGridError(
    val file: String,
    val error: GridError
)

data class GridError(
    val error: String,
    val debugInfo: String
)
