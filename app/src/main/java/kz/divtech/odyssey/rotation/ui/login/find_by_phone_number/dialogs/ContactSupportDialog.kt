package kz.divtech.odyssey.rotation.ui.login.find_by_phone_number.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kz.divtech.odyssey.rotation.R
import kz.divtech.odyssey.rotation.databinding.DialogContactSupportBinding
import kz.divtech.odyssey.rotation.ui.MainActivity
import kz.divtech.odyssey.rotation.ui.help.contact_support.ContactSupportViewModel
import kz.divtech.odyssey.rotation.common.utils.ContactUtil


class ContactSupportDialog : BottomSheetDialogFragment() {
    val viewModel: ContactSupportViewModel by viewModels{
        ContactSupportViewModel.ContactSupportViewModelFactory(
            (activity as MainActivity).orgInfoRepository)
    }
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dataBinding  = DialogContactSupportBinding.inflate(inflater)
        dataBinding.thisDialog = this

        viewModel.orgInfoLiveData.observe(viewLifecycleOwner){
            it?.let{ orgInfo ->
                dataBinding.callSupportBtn.setOnClickListener{
                    ContactUtil.callSupport(this, orgInfo.supportPhone)
                }
                dataBinding.writeWhatsappBtn.setOnClickListener{
                    ContactUtil.writeSupportOnWhatsapp(this, orgInfo.whatsappPhone)
                }
                dataBinding.writeTelegramBtn.setOnClickListener{
                    orgInfo.telegramId?.let { it1 ->
                        ContactUtil.writeSupportOnTelegram(this, it1)
                    }
                }
            }
        }

        return dataBinding.root
    }

}