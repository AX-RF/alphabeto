package com.alphabeto.ui.french

import com.alphabeto.R
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.ui.common.BaseAlphabetFragment

class FrenchAlphabetFragment : BaseAlphabetFragment() {
    override fun getLanguage(): AlphabetLanguage = AlphabetLanguage.FRENCH

    override fun getToolbarTitle(): String = getString(R.string.title_french_alphabet)
}
