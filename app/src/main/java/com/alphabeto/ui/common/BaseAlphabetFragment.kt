package com.alphabeto.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alphabeto.R
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.databinding.FragmentAlphabetGridBinding
import com.alphabeto.di.ServiceLocator
import com.alphabeto.utils.SoundManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class BaseAlphabetFragment : Fragment() {

    private var _binding: FragmentAlphabetGridBinding? = null
    protected val binding get() = _binding!!

    private val observeLettersUseCase by lazy {
        ServiceLocator.provideObserveLettersUseCase(requireContext())
    }

    private val resetAlphabetProgressUseCase by lazy {
        ServiceLocator.provideResetAlphabetProgressUseCase(requireContext())
    }

    private val viewModel: AlphabetViewModel by viewModels {
        AlphabetViewModelFactory(getLanguage(), observeLettersUseCase)
    }

    private val adapter = LetterGridAdapter { letterWithProgress ->
        SoundManager.play(letterWithProgress.letter.soundResourceId)
        val actionId = when (getLanguage()) {
            AlphabetLanguage.ARABIC -> R.id.action_arabic_to_tracing
            AlphabetLanguage.FRENCH -> R.id.action_french_to_tracing
        }
        val bundle = Bundle().apply {
            putString("language", getLanguage().name)
            putString("character", letterWithProgress.letter.character)
        }
        findNavController().navigate(actionId, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlphabetGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        SoundManager.resumeAll()
    }

    override fun onPause() {
        SoundManager.pauseAll()
        super.onPause()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            title = getToolbarTitle()
            setNavigationOnClickListener { findNavController().navigateUp() }
            inflateMenu(R.menu.menu_alphabet_actions)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_reset_progress -> {
                        viewLifecycleOwner.lifecycleScope.launch {
                            resetAlphabetProgressUseCase(getLanguage())
                            Snackbar.make(
                                binding.root,
                                R.string.progress_reset_success,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.letterRecyclerView.apply {
            val spanCount = getSpanCount()
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            if (itemDecorationCount == 0) {
                val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)
                addItemDecoration(GridSpacingItemDecoration(spanCount, spacing))
            }
            setHasFixedSize(true)
            adapter = this@BaseAlphabetFragment.adapter
        }
    }

    private fun observeState() {
        viewModel.state
            .onEach { uiState ->
                binding.progressBar.isVisible = uiState.isLoading
                val hasData = uiState.letters.isNotEmpty()
                val hasError = uiState.error != null
                binding.emptyStateText.isVisible = !hasData && !uiState.isLoading
                binding.emptyStateText.text = when {
                    hasError -> getString(R.string.alphabet_error_loading)
                    else -> getString(R.string.prompt_select_letter)
                }
                adapter.submitList(uiState.letters)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    protected open fun getSpanCount(): Int {
        val smallestWidth = resources.configuration.smallestScreenWidthDp
        return if (smallestWidth >= 600) 5 else 2
    }

    protected abstract fun getLanguage(): AlphabetLanguage

    protected abstract fun getToolbarTitle(): String

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
