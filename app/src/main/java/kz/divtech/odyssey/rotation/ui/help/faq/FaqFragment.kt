package kz.divtech.odyssey.rotation.ui.help.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kz.divtech.odyssey.rotation.R
import kz.divtech.odyssey.rotation.data.remote.result.asFailure
import kz.divtech.odyssey.rotation.data.remote.result.isFailure
import kz.divtech.odyssey.rotation.databinding.FragmentFaqBinding
import kz.divtech.odyssey.rotation.domain.model.EmptyData
import kz.divtech.odyssey.rotation.ui.MainActivity
import java.net.UnknownHostException

class FaqFragment : Fragment() {
    private var _binding : FragmentFaqBinding? = null
    private val binding get() = _binding!!
    internal val viewModel: FaqViewModel by viewModels {
            FaqViewModel.FaqViewModelFactory((activity as MainActivity).faqRepository)
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFaqBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.emptyData =  EmptyData(ContextCompat.getDrawable(requireContext(), R.drawable.icon_faq)!!,
            requireContext().getString(R.string.empty_faq_title),
            requireContext().getString(R.string.empty_faq_content))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val faqAdapter = FaqAdapter()
        binding.faqRecyclerView.adapter = faqAdapter

        viewModel.faqResult.observe(viewLifecycleOwner){ result ->
            result?.let{
                if(result.isFailure() && result.asFailure().error !is UnknownHostException){
                    Toast.makeText(requireContext(), "$result", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getFaqListFromServer()

        viewModel.faqLiveData.observe(viewLifecycleOwner) { faqList ->
            binding.faqSearchView.isVisible = faqList.isNotEmpty()
            binding.emptyFaq.root.isVisible = faqList.isEmpty()
            faqAdapter.setList(faqList)
        }

        binding.faqSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    lifecycleScope.launch {
                        val faqList = viewModel.searchFaqFromDB(text)
                        faqAdapter.setList(faqList)
                    }
                }
                return true
            }

        })
    }

    override fun onResume() {
        super.onResume()

        val bundle = bundleOf()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, getString(R.string.questions_and_answers))
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "FaqFragment")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}