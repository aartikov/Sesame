package me.aartikov.sesamesample.form

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentFormBinding

@AndroidEntryPoint
class FormFragment : BaseFragment<FormViewModel>(R.layout.fragment_form, FormViewModel::class) {

    private val binding by viewBinding(FragmentFormBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            vm.nameInput bind nameInputLayout
            vm.emailInput bind emailInputLayout
            vm.phoneInput bind phoneInputLayout
            vm.passwordInput bind passwordInputLayout
            vm.confirmPasswordInput bind confirmPasswordInputLayout
        }
    }
}

