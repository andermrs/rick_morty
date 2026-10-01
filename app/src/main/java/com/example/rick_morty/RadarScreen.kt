package com.example.rick_morty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarScreen(viewModel: CharacterViewModel = viewModel()) {
    val characters by viewModel.charactersState.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(syncState) {
        if (syncState is SyncState.Error) {
            snackbarHostState.showSnackbar((syncState as SyncState.Error).message)
            viewModel.resetSyncState()
        }
    }

    Scaffold(
        snackbarHostState = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Multiverse Radar 🪐") },
                actions = {
                    IconButton(
                        onClick = { viewModel.syncData() },
                        enabled = syncState !is SyncState.Loading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (characters.isEmpty() && syncState is SyncState.Idle) {
                Text(
                    text = "Nenhum personagem encontrado. Clique em sincronizar.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(characters, key = { it.id }) { character ->
                        CharacterCard(character)
                    }
                }
            }

            if (syncState is SyncState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun CharacterCard(character: CharacterEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(text = character.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Espécie: ${character.species}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}