package app.simple.inure.ui.panels

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import app.simple.inure.R
import app.simple.inure.activities.app.MainActivity
import app.simple.inure.adapters.ui.AdapterTags
import app.simple.inure.constants.BottomMenuConstants
import app.simple.inure.constants.ShortcutConstants
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.extensions.fragments.ScopedFragment
import app.simple.inure.models.Tag
import app.simple.inure.popups.tags.PopupTagsMenu
import app.simple.inure.ui.subpanels.TaggedApps
import app.simple.inure.util.StatusBarHeight
import app.simple.inure.viewmodels.panels.TagsViewModel

class Tags : ScopedFragment() {

    private lateinit var recyclerView: CustomVerticalRecyclerView

    private var tagsViewModel: TagsViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tags, container, false)

        recyclerView = view.findViewById(R.id.tags_recycler_view)

        tagsViewModel = ViewModelProvider(requireActivity())[TagsViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fullVersionCheck()
        postponeEnterTransition()

        tagsViewModel?.getTags()?.observe(viewLifecycleOwner) {
            val adapter = AdapterTags(it, object : AdapterTags.Companion.TagsCallback {
                override fun onTagClicked(tag: Tag) {
                    openFragmentSlide(TaggedApps.newInstance(tag.tag), "tagged_apps")
                }

                override fun onTagLongClicked(tag: Tag) {
                    PopupTagsMenu(requireView(), object : PopupTagsMenu.Companion.TagsMenuCallback {
                        override fun onOpenClicked() {
                            openFragmentSlide(TaggedApps.newInstance(tag.tag), "tagged_apps")
                        }

                        override fun onDeleteClicked() {
                            onSure {
                                (recyclerView.adapter as AdapterTags).removeTag(tag)
                                tagsViewModel?.deleteTag(tag)
                            }
                        }

                        override fun onCreateShortcutClicked() {
                            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                                action = ShortcutConstants.TAGGED_APPS_ACTION
                                putExtra(ShortcutConstants.TAGGED_APPS_EXTRA, tag.tag)
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }

                            val shortcut = ShortcutInfoCompat.Builder(requireContext(), tag.tag)
                                .setShortLabel(tag.tag)
                                .setActivity(ComponentName(requireContext(), MainActivity::class.java))
                                .setIcon(IconCompat.createWithResource(requireContext(), R.drawable.sc_tags))
                                .setIntent(intent)
                                .build()

                            ShortcutManagerCompat.requestPinShortcut(requireContext(), shortcut, null)
                        }
                    })
                }
            })

            val spanCount = if (StatusBarHeight.isLandscape(requireContext())) {
                3
            } else {
                2
            }

            recyclerView.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }

            recyclerView.adapter = adapter

            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }

            bottomRightCornerMenu?.initBottomMenuWithRecyclerView(BottomMenuConstants.getGenericBottomMenuItems(), recyclerView) { id, _ ->
                when (id) {
                    R.drawable.ic_settings -> {
                        openFragmentSlide(Preferences.newInstance(), "preferences")
                    }
                    R.drawable.ic_search -> {
                        openFragmentSlide(Search.newInstance(true), "search")
                    }
                    R.drawable.ic_refresh -> {
                        tagsViewModel?.refresh()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): Tags {
            val args = Bundle()
            val fragment = Tags()
            fragment.arguments = args
            return fragment
        }
    }
}