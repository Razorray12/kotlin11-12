package com.example.kotlin10

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.kotlin10.ui.theme.Kotlin10Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kotlin10Theme {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(Modifier.height(16.dp))
                            Text("Item 1", Modifier.padding(16.dp))
                            Text("Item 2", Modifier.padding(16.dp))
                            Text("Item 3", Modifier.padding(16.dp))
                        }
                    },
                    drawerState = drawerState
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { Text("Главная") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { /* Handle action icon press */ }) {
                                        Icon(Icons.Filled.Search, contentDescription = "Search")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            BottomAppBar {
                                IconButton(onClick = { /* Handle icon click */ })
                                {
                                    Icon(
                                        Icons.Filled.Menu, contentDescription =
                                        "Menu"
                                    )
                                }
                                Spacer(Modifier.weight(1f, true))
                                IconButton(onClick = { /* Handle icon click */ })
                                {
                                    Icon(
                                        Icons.Filled.Favorite, contentDescription
                                        = "Favorite"
                                    )
                                }
                                IconButton(onClick = { /* Handle icon click */ })
                                {
                                    Icon(
                                        Icons.Filled.Share, contentDescription =
                                        "Share"
                                    )
                                }
                            }
                        },
                    ) { innerPadding ->
                        MyApp(innerPadding, ::scheduleImageDownload)
                    }
                }
            }
        }
    }

    private fun scheduleImageDownload(imageUrl: String) {
        val workRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setInputData(workDataOf("imageUrl" to imageUrl))
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

}
@Composable
fun OtherScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Привет")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("home") }) {
            Text(text = "Go to Home")
        }
    }
}

@Composable
fun ImageDownloaderScreen(
    navController: NavController,
    imageUrlState: MutableState<String>,
    bitmapState: MutableState<Bitmap?>,
    messageState: MutableState<String?>,
    modifier: Modifier,
    onDownloadClick: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = imageUrlState.value,
            onValueChange = { imageUrlState.value = it },
            label = { Text("Введите URL изображения") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onDownloadClick(imageUrlState.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Загрузить изображение")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("detail") }) {
            Text(text = "Go to Detail")
        }
        Spacer(modifier = Modifier.height(16.dp))
        bitmapState.value?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Загруженное изображение",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    messageState.value?.let {
        Text(text = it)
    }
}

@Composable
fun MyApp(paddingValues: PaddingValues, onDownloadClick: (String) -> Unit) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
        composable("home") {
            ImageDownloaderScreen(
                navController = navController,
                imageUrlState = remember { mutableStateOf("") },
                bitmapState = remember { mutableStateOf(null) },
                messageState = remember { mutableStateOf(null) },
                modifier = Modifier,
                onDownloadClick = onDownloadClick
            )
        }
        composable("detail") {
            OtherScreen(navController)
        }
    }
}