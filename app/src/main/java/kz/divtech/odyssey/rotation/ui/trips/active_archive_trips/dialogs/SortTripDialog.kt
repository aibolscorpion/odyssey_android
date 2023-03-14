package kz.divtech.odyssey.rotation.ui.trips.active_archive_trips.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kz.divtech.odyssey.rotation.R
import kz.divtech.odyssey.rotation.databinding.DialogSortBinding

class SortTripDialog : BottomSheetDialogFragment() {
    override fun getTheme() = R.style.BottomSheetDialogTheme

    override fun getDialog() = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogSortBinding.inflate(inflater)
        binding.thisDialog = this
        return binding.root
    }
}