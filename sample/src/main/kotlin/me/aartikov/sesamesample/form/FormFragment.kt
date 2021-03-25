package me.aartikov.sesamesample.form

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.aartikov.sesame.localizedstring.localizedText
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.base.BaseFragment
import me.aartikov.sesamesample.databinding.FragmentFormBinding
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

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
            vm.termsCheckBox bind termsCheckbox
            vm.termsCheckBox::error bind { termsError.localizedText = it }
            submitButton.setOnClickListener { vm.onSubmitClicked() }
            vm.dropKonfetti bind { binding.konfetti.drop() }
        }
    }

    private fun KonfettiView.drop() {
        build()
            .addColors(
                color(R.color.orange),
                color(R.color.purple),
                color(R.color.pink),
                color(R.color.red)
            )
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, this.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)
    }

    private fun color(resId: Int): Int = ContextCompat.getColor(requireContext(), resId)
}
