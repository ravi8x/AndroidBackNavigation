package info.androidhive.androidbacknavigation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import info.androidhive.androidbacknavigation.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val backPressCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showConfirmationDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Showing back press confirmation when form has unsaved data
     * */
    private fun showConfirmationDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it).setTitle(resources.getString(R.string.title))
                .setMessage(resources.getString(R.string.unsaved_message))
                .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

                }.setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    findNavController().popBackStack()
                }.show()
        }
    }

    private var textChangeListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // toggle back press callback when form data is changed
            toggleBackPress()
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    /**
     * Enable back press callback when form has unsaved data
     * */
    private fun toggleBackPress() {
        backPressCallback.isEnabled =
            !binding.name.text.isNullOrBlank() || !binding.email.text.isNullOrBlank()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            findNavController().popBackStack()
        }

        activity?.onBackPressedDispatcher?.addCallback(backPressCallback)

        // disable back press callback by default
        backPressCallback.isEnabled = false

        initForm()
    }

    private fun initForm() {
        binding.apply {
            name.addTextChangedListener(textChangeListener)
            email.addTextChangedListener(textChangeListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // removing callback is always not necessary
        // but while using navigation component, the older listener still attached
        // after back navigation happens
        backPressCallback.remove()
    }
}