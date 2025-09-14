package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.appquests.droneprep.data.model.Acronym
import com.appquests.droneprep.di.AppGraph
import com.appquests.droneprep.ui.design.DS
import com.appquests.droneprep.ui.design.Palette
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcronymBusterScreen(navController: NavController) {
    val repo = remember { AppGraph.questionRepository }
    var acronyms by remember { mutableStateOf<List<Acronym>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredAcronyms = remember(acronyms, searchQuery) {
        if (searchQuery.isBlank()) {
            acronyms
        } else {
            acronyms.filter { 
                it.acronym.contains(searchQuery, ignoreCase = true) ||
                it.definition.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Load acronyms
    LaunchedEffect(Unit) {
        acronyms = repo.loadAcronyms()
    }

    Scaffold(
        modifier = Modifier.background(Palette.Bg),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Acronym Buster",
                        color = Palette.TextPrimary,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = Palette.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Palette.Bg,
                    titleContentColor = Palette.TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Palette.Bg)
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Search bar
                Surface(
                    color = Palette.Card,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DS.SpaceLg, vertical = DS.SpaceMd)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                "Search acronyms...",
                                color = Palette.TextSecondary
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Palette.TextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    keyboardController?.hide()
                                }) {
                                    Icon(
                                        Icons.Filled.Clear,
                                        contentDescription = "Clear",
                                        tint = Palette.TextSecondary
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Palette.BrandYellow,
                            unfocusedBorderColor = Palette.TextSecondary.copy(alpha = 0.3f),
                            focusedTextColor = Palette.TextPrimary,
                            unfocusedTextColor = Palette.TextPrimary,
                            cursorColor = Palette.BrandYellow
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { keyboardController?.hide() }
                        ),
                        singleLine = true
                    )
                }

                // Acronyms list
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = DS.SpaceLg),
                    verticalArrangement = Arrangement.spacedBy(DS.SpaceSm)
                ) {
                    itemsIndexed(filteredAcronyms) { index, acronym ->
                        AcronymItem(acronym = acronym)
                    }
                    
                    // Bottom padding
                    item {
                        Spacer(modifier = Modifier.height(DS.SpaceLg))
                    }
                }
            }

            // A-Z Index Navigator
            if (acronyms.isNotEmpty() && searchQuery.isBlank()) {
                AZIndexNavigator(
                    acronyms = acronyms,
                    listState = listState,
                    onLetterTap = { targetIndex ->
                        coroutineScope.launch {
                            listState.animateScrollToItem(targetIndex)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AcronymItem(acronym: Acronym) {
    Surface(
        color = Palette.Card,
        shape = RoundedCornerShape(DS.RadiusCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(DS.SpaceMd)
        ) {
            Text(
                text = acronym.acronym,
                color = Palette.BrandYellow,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = acronym.definition,
                color = Palette.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun AZIndexNavigator(
    acronyms: List<Acronym>,
    listState: LazyListState,
    onLetterTap: (Int) -> Unit
) {
    // Create a map of letters to their first occurrence index
    val letterIndexMap = remember(acronyms) {
        val map = mutableMapOf<Char, Int>()
        acronyms.forEachIndexed { index, acronym ->
            val firstChar = acronym.acronym.firstOrNull()?.uppercaseChar()
            if (firstChar != null && firstChar.isLetter() && !map.containsKey(firstChar)) {
                map[firstChar] = index
            }
        }
        map
    }

    val availableLetters = letterIndexMap.keys.sorted()
    val allLetters = ('A'..'Z').toList()

    Column(
        modifier = Modifier
            .width(32.dp)
            .fillMaxHeight()
            .padding(vertical = DS.SpaceMd, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        allLetters.forEach { letter ->
            val isAvailable = letter in availableLetters
            val targetIndex = letterIndexMap[letter] ?: 0
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(enabled = isAvailable) {
                        if (isAvailable) {
                            onLetterTap(targetIndex)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.toString(),
                    color = if (isAvailable) Palette.BrandYellow else Palette.TextSecondary.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isAvailable) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
