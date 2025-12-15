package com.alphabeto.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alphabeto.R
import com.alphabeto.databinding.FragmentMainMenuBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainMenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        observeEvents()
    }

    private fun setupUi() = with(binding) {
        arabicButton.setOnClickListener { viewModel.onArabicSelected() }
        frenchButton.setOnClickListener { viewModel.onFrenchSelected() }
    }

    private fun observeEvents() {
        viewModel.events
            .onEach { event ->
                when (event) {
                    MainMenuEvent.NavigateToArabic -> findNavController().navigate(R.id.action_mainMenu_to_arabic)
                    MainMenuEvent.NavigateToFrench -> findNavController().navigate(R.id.action_mainMenu_to_french)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
