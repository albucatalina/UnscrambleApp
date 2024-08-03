package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())

    //Backing property
    val uiState : StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    fun checkUserGuess(){
        if(userGuess.equals(currentWord, ignoreCase = true)){
            // User's guess is correct, increase the score
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        }else{
            // User's guess is wrong, show an error
            _uiState.update {
                currentState -> currentState.copy(isGuessedWordWrong = true) //copy() allows to alter some of the object property while keeping the rest unchanged
            }
        }
        //reset user guess
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int){
        //prepare the game for the next round
        _uiState.update {
            currentState -> currentState.copy(
                currentScrambleWord = pickRandomWordAndShuffle(),
                currentWordCount = currentState.currentWordCount.inc(),
                score = updatedScore,
                isGuessedWordWrong = false
            )
        }
    }

    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambleWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessWord: String){
        userGuess = guessWord
    }

    fun pickRandomWordAndShuffle() : String {
        currentWord = allWords.random()
        if(usedWords.contains(currentWord)){
            return pickRandomWordAndShuffle()
        }else{
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(currentWord: String): String {
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        while(String(tempWord) == currentWord){
            tempWord.shuffle()
        }
        return String(tempWord)
    }
}