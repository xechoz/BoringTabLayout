# BoringTabLayout



https://github.com/user-attachments/assets/3c0a6984-ce76-4e42-94f5-ddf9616a245d


A simplified version of TabLayout, only addTab, onTabSelected, setIndicator.  
If it does not meet your usage scenario, you should use the official TabLayout

BoringTabLayout is a LinearLayout, 通过 监听 ViewPager 的 page 切换事件，切换选中的 tab view，仅此而已

Specifically, the following logic is implemented:
1. add any views as tabs
2. When the ViewPager page is switched, the current tab view is switched at the same time. Specifically, it is done through `ViewPager.addOnPageChangeListener` or `ViewPager2.registerOnPageChangeCallback`
3. Click the tab to switch the ViewPager currentItem

# Usage


1. create views of tabs: your list of views as tabs
2. tabLayout.setTabs(viewPager: ViewPager, tabs: List<View>)
3. Listen to tab selection status changes. override yourTabView.setSelected: change your tab style when selected changed
4. (optional) tabLayout.setIndicator(DrawIndicator)
5. (optional) BoringTabLayout is just a LinearLayout, and can use any LinearLayout attributes in your xml file

Demo

```kotlin
class YourViewContainsTheTabLayout: FrameLayout {
    fun initTabLayout(
        tabLayout: BoringTabLayout,
        viewPager: ViewPager2,
        tabsData: List<FooTabData>
    ) {
        // 1. create tabs from data
        val tabViews = tabsData.map { tabData ->
            val tabView = FooTabView(this) // your custom tab view
            tabView.setData(tabData)
            // set layout params as your need
            // tabView.layoutParams = LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT)
            return@map tabView
        }

        // 2. add to layout
        tabLayout.setTabs(viewPager = viewPager, tabs = tabViews)

        // 3. (optional) custom indicator, you can combine multi indicators 
        tabLayout.drawIndicator = SwipeAnimateIndicator(
            tabViews, LineIndicator(
                tabViews, 100, 20, Color.GREEN, 16f,
                marginBottom = 8f
            )
        )
    }
}

private class FooTabView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding = TabViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(tabData: TabData) {
        binding.title.text = tabData.title
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)

        // on tab selected changed, change your tab style
        // change text size, text font, text color, etc.
        binding.title.typeface = if (selected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.tabRoot.setBackgroundColor(if (selected) Color.YELLOW else Color.WHITE)
    }
}
```

custom attributes
```xml
    <io.github.xechoz.BoringTabLayout
        android:id="@+id/tab_layout"
        android:background="@color/purple_500"
        android:elevation="8dp"
        android:gravity="center"
        android:layout_width="match_parent"
        android:layout_height="64dp"/>

```

See Demo code in `MainActivity`



