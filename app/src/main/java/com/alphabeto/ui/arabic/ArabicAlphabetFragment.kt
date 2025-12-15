package com.alphabeto.ui.arabic

import com.alphabeto.R
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.ui.common.BaseAlphabetFragment

class ArabicAlphabetFragment : BaseAlphabetFragment() {
    override fun getLanguage(): AlphabetLanguage = AlphabetLanguage.ARABIC

    override fun getToolbarTitle(): String = getString(R.string.title_arabic_alphabet)
}
