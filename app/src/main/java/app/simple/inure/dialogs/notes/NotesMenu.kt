package app.simple.inure.dialogs.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.decorations.switchview.SwitchView
import app.simple.inure.extensions.fragments.ScopedBottomSheetFragment
import app.simple.inure.preferences.NotesPreferences

class NotesMenu : ScopedBottomSheetFragment() {

    private lateinit var expandedNotes: SwitchView
    private lateinit var openSettings: DynamicRippleTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_menu_notes, container, false)

        expandedNotes = view.findViewById(R.id.expanded_notes)
        openSettings = view.findViewById(R.id.dialog_open_apps_settings)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandedNotes.setChecked(NotesPreferences.areNotesExpanded())

        expandedNotes.setOnSwitchCheckedChangeListener {
            NotesPreferences.setExpandedNotes(it)
        }

        openSettings.setOnClickListener {
            openSettings()
        }
    }

    companion object {
        fun newInstance(): NotesMenu {
            val args = Bundle()
            val fragment = NotesMenu()
            fragment.arguments = args
            return fragment
        }
    }
}