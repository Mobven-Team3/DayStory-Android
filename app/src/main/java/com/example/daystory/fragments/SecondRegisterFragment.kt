package com.example.daystory.fragments

import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.daystory.R
import com.example.daystory.databinding.FragmentSecondRegisterBinding


class SecondRegisterFragment : Fragment() {

    private lateinit var binding: FragmentSecondRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSecondRegisterBinding.inflate(inflater, container, false)

        binding.backIcon.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            it.findNavController().navigate(R.id.action_secondRegisterFragment_to_loginFragment)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val registerText = getString(R.string.register_prompt)
        val spannableString = SpannableString(registerText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.findNavController()
                    .navigate(R.id.action_secondRegisterFragment_to_loginFragment)
            }
        }

        val startIndex = registerText.indexOf("Giriş yap")
        val endIndex = startIndex + "Giriş yap".length

        spannableString.setSpan(clickableSpan, startIndex, endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textGirisyap.text = spannableString
        binding.textGirisyap.movementMethod = LinkMovementMethod.getInstance()
    }

}