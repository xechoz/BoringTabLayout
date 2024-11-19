package io.github.xechoz

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

/**
 * Simplified TabLayout, add any view as tabs, change tab style by your need
 *
 * This class only does the following steps:
 *
 * 1. add views as tabs
 * 2. switch view pager if tab click
 * 3. switch tab if view pager switch by add lisenter to ViewPager's page change event
 *
 * **do not support scroll tabs**, if you need a scrollable tabs, use TabLayout by androidx library
 *
 * usage:
 * 1. setTabs: add your tab views
 * 2. override yourTabView.setSelected: change you tab style on selected changed
 *
 * ```
 * // set tabs in your container class
 * class YourViewContainTabsLayout: SomeView {
 *  private fun initTabLayout(tabLayout: BoringTabLayout, viewPager: ViewPager2) {
 *         // create tabs from data
 *        val tabViews = tabsData.map { tabData ->
 *             val tabView = FooTabView(this) // your custom tab view
 *             tabView.setData(tabData)
 *             // set layout params as your need
 *             // tabView.layoutParams = LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT)
 *            return@map tabView
 *         }
 *
 *         // add to layout
 *         tabLayout.setTabs(viewPager = viewPager, tabs = tabViews)
 *  }
 * }
 *
 * // your FooTabView
 * class FooTabView : SomeView {
 *
 *  // on tab selected changed, change your tab style
 *  override fun setSelected(selected: Boolean) {
 *         super.setSelected(selected)
 *
 *         binding.title.textSize = if (selected) 32f else 24f
 *         binding.title.typeface = if (selected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
 *         binding.tabRoot.setBackgroundColor(if (selected) Color.YELLOW else Color.WHITE)
 *     }
 * }
 * ```
 */
class BoringTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var fromPosition = -1
    private val scrolledState = PageScrolledState(-1, 0f, 0)

    /**
     * call by onDraw(Canvas), invalidate when a page scroll horizon
     * default as an empty function, do nothing
     */
    var drawIndicator: DrawIndicator = { _, _, _, _ -> }

    /**
     * invoke when a tab is click or view pager switch a page
     *
     * It is recommended to use the following interface to listen tab change events:
     *
     * androidx.viewpager.widget.ViewPager.addOnPageChangeListener
     *
     * androidx.viewpager2.widget.ViewPager2.registerOnPageChangeCallback
     */
    var onTabSelected: (View, Int) -> Unit = { _, _ -> }

    /**
     * set tab views,
     * auto change tab when view pager switch;
     * auto change view pager on tab click
     *
     * yourTabView.setSelected is call when a tab selected changed
     */
    fun setTabs(viewPager: ViewPager, tabs: List<View>) {
        removeAllTabs()
        tabs.forEach { tabView ->
            addView(tabView)

            tabView.setOnClickListener {
                // select tab
                viewPager.currentItem = tabs.indexOf(tabView)
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                this@BoringTabLayout.onPageSelected(position, tabs)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                // trigger onDraw, to draw a custom indicator
                scrolledState.position = position
                scrolledState.positionOffset = positionOffset
                scrolledState.positionOffsetPixels = positionOffsetPixels
                invalidate()
            }
        })
    }

    fun setTabs(viewPager: ViewPager2, tabs: List<View>) {
        removeAllTabs()

        tabs.forEach { tabView ->
            addView(tabView)

            tabView.setOnClickListener {
                // select tab
                viewPager.currentItem = tabs.indexOf(tabView)
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                this@BoringTabLayout.onPageSelected(position, tabs)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                // trigger onDraw, to draw a custom indicator
                scrolledState.position = position
                scrolledState.positionOffset = positionOffset
                scrolledState.positionOffsetPixels = positionOffsetPixels
                invalidate()
            }
        })
    }

    private fun removeAllTabs() {
        fromPosition = -1
        removeAllViews()
    }

    private fun onPageSelected(position: Int, tabs: List<View>) {
        if (position == fromPosition || position == -1) {
            return
        }

        // unselected
        if (fromPosition != -1) {
            tabs[fromPosition].isSelected = false
        }

        // seleted
        fromPosition = position
        tabs[position].isSelected = true
        onTabSelected(tabs[position], position)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (scrolledState.position != -1) {
            drawIndicator(
                canvas,
                scrolledState.position,
                scrolledState.positionOffset,
                scrolledState.positionOffsetPixels
            )
        }

        super.dispatchDraw(canvas)
    }
}

private class PageScrolledState(
    var position: Int,
    var positionOffset: Float,
    var positionOffsetPixels: Int
)