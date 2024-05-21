package com.example.daystory.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.daystory.MainActivity
import com.example.daystory.R
import com.example.daystory.databinding.FragmentEditEventBinding
import com.example.daystory.model.Event
import com.example.daystory.viewmodel.EventViewModel

class EditEventFragment : Fragment(R.layout.fragment_edit_event), MenuProvider {

    private var editEventbinding: FragmentEditEventBinding? = null
    private val binding get() = editEventbinding!!

    private lateinit var eventsViewModel: EventViewModel
    private lateinit var currentEvent: Event

    private val args: EditEventFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editEventbinding = FragmentEditEventBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        eventsViewModel = (activity as MainActivity).eventViewModel
        currentEvent = args.event!!

        binding.editEventTitle.setText(currentEvent.eventTitle)
        binding.editEventDesc.setText(currentEvent.eventDesc)

        binding.editBackIcon.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.iconDelete.setOnClickListener {
            deleteEvent()
        }

        binding.btnSave.setOnClickListener {
            val eventTitle = binding.editEventTitle.text.toString().trim()
            val eventDesc = binding.editEventDesc.text.toString().trim()

                val event = Event(currentEvent.id, eventTitle, eventDesc)
                eventsViewModel.updateEvent(event)

        }
    }

    private fun deleteEvent(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Event")
            setMessage("Do you want to delete this event?")
            setPositiveButton("Delete"){_,_ ->
                eventsViewModel.deleteEvent(currentEvent)
                Toast.makeText(context,"Event Deleted",Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_event, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        editEventbinding = null
    }

}