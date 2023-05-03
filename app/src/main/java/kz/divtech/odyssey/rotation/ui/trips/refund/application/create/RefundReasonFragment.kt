package kz.divtech.odyssey.rotation.ui.trips.refund.application.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import kz.divtech.odyssey.rotation.R
import kz.divtech.odyssey.rotation.data.remote.result.asSuccess
import kz.divtech.odyssey.rotation.data.remote.result.isSuccess
import kz.divtech.odyssey.rotation.databinding.FragmentRefundReasonBinding
import kz.divtech.odyssey.rotation.ui.MainActivity
import kz.divtech.odyssey.rotation.ui.trips.refund.application.RefundViewModel
import kz.divtech.odyssey.rotation.utils.KeyboardUtils

class RefundReasonFragment: Fragment() {
    private val args : RefundReasonFragmentArgs by navArgs()
    lateinit var binding: FragmentRefundReasonBinding
    val viewModel: RefundViewModel by viewModels{
        RefundViewModel.RefundViewModelFactory((activity as MainActivity).refundRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRefundReasonBinding.inflate(inflater)
        binding.thisDialog = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRadioGroup()
    }

    private fun setRadioGroup(){
        binding.reasonsRG.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.userVariantRB -> {
                    KeyboardUtils.showKeyboard(requireContext(), binding.userVariantET)
                }
                else -> {
                    KeyboardUtils.hideKeyboard(requireContext(), binding.userVariantET)
                    binding.userVariantET.clearFocus()
                }
            }
        }

        binding.userVariantET.setOnFocusChangeListener{ _, hasFocus ->
            if(hasFocus){
                binding.userVariantRB.isChecked = true
            }
        }
    }

    fun sendApplicationToRefund(){
        if(getReasonOfRefund().isEmpty()) {
            Toast.makeText(requireContext(), R.string.specify_refund_reason, Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val result = viewModel.sendApplicationToRefund(args.applicationId, args.segmentId,
                getReasonOfRefund())
            if(result.isSuccess()){
                val refundId = result.asSuccess().value["id"]!!
                openRefundSendFragment(refundId)
            }else{
                Toast.makeText(requireContext(), R.string.error_happened, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getReasonOfRefund(): String{
        val checkedRadioId = binding.reasonsRG.checkedRadioId
        return if(checkedRadioId != -1){
            val checkedRadioBtn = binding.reasonsRG.findViewById<RadioButton>(checkedRadioId)
            if(checkedRadioId == binding.userVariantRB.id) {
                binding.userVariantET.text.toString()
            }else {
                checkedRadioBtn.text.toString()
            }
        }else{
            ""
        }
    }

    private fun openRefundSendFragment(refundId: Int) {
        findNavController().navigate(
            RefundReasonFragmentDirections.actionRefundReasonFragmentToRefundSendFragment(true, refundId))
    }

}