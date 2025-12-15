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
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.databinding.FragmentLetterTracingBinding
import com.alphabeto.di.ServiceLocator
import com.alphabeto.utils.SoundManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LetterTracingFragment : Fragment(), LetterTracingView.TracingListener {

    private var _binding: FragmentLetterTracingBinding? = null
    private val binding get() = _binding!!

    private val argsLanguage: AlphabetLanguage by lazy {
        val languageName = requireArguments().getString(ARG_LANGUAGE).orEmpty()
        AlphabetLanguage.valueOf(languageName)
    }

    private val argsCharacter: String by lazy {
        requireArguments().getString(ARG_CHARACTER).orEmpty()
    }

    private val getLetterDetailsUseCase by lazy {
        ServiceLocator.provideGetLetterDetailsUseCase(requireContext())
    }

    private val updateLetterProgressUseCase by lazy {
        ServiceLocator.provideUpdateLetterProgressUseCase(requireContext())
    }

    private val viewModel: LetterTracingViewModel by viewModels {
        LetterTracingViewModelFactory(
            language = argsLanguage,
            character = argsCharacter,
            getLetterDetailsUseCase = getLetterDetailsUseCase,
            updateLetterProgressUseCase = updateLetterProgressUseCase,
            applicationContext = requireContext().applicationContext
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLetterTracingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
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

    private fun setupUi() = with(binding) {
        letterTracingView.letterCharacter = argsCharacter
        letterTracingView.listener = this@LetterTracingFragment
        repeatSoundButton.setOnClickListener { viewModel.onRepeatSoundRequested() }
        clearButton.setOnClickListener { viewModel.onClearRequested() }
        restartButton.setOnClickListener { viewModel.onRestartRequested() }
        backButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun observeState() {
        viewModel.uiState
            .onEach { state ->
                binding.progressIndicator.isVisible = state.isLoading
                binding.letterTracingView.isEnabled = !state.isLoading
                binding.letterTracingView.letterCharacter = state.letterTitle
                binding.repeatSoundButton.isEnabled = !state.isLoading && state.isSoundAvailable
                binding.clearButton.isEnabled = !state.isLoading
                binding.restartButton.isEnabled = !state.isLoading
                binding.letterTitle.text = state.letterTitle
                binding.tracingStats.text = state.progressDescription
                state.soundToPlay?.let { resId ->
                    SoundManager.play(resId)
                    viewModel.onSoundPlayed()
                }
                if (state.shouldClearCanvas) {
                    binding.letterTracingView.clearTracing()
                    viewModel.onCanvasCleared()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onStrokeCompleted(strokes: Int, totalLength: Float) {
        viewModel.onStrokeCompleted(strokes, totalLength)
    }

    override fun onTracingCleared() {
        viewModel.onTracingCleared()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SoundManager.pauseAll()
        binding.letterTracingView.listener = null
        _binding = null
    }

    companion object {
        private const val ARG_LANGUAGE = "language"
        private const val ARG_CHARACTER = "character"
    }
}
