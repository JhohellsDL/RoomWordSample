package com.jdlstudios.roomwordsample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    //val allWords: LiveData<List<Word>> = repository.allWords
    private var _allWords = MutableLiveData<List<Word>>()
    val allWords: LiveData<List<Word>>
        get() = _allWords



    /*private val _allWords = MutableLiveData<List<Word>>().postValue(
        repository.allWords.asFlow()
    )*/
    /*val allWords: LiveData<List<Word>>
        get() = _allWords
*/
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    //-----------------------------------para coroutinas------------------------------------------------
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //--------------------------------------------------------------------------------------------------
    init {
        getValorList()
    }

    fun getValorList() {
        uiScope.launch {
            repository.allWords.let { listFlow ->
                listFlow.collect {
                    _allWords.value = it
                }
            }
        }
    }

    fun insert(word: Word) {
        uiScope.launch {
            repository.insert(word)
        }
    }

    class WordViewModelFactory(private val repository: WordRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WordViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}