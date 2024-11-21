package io.github.xechoz.boring

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.github.xechoz.BoringTabLayout
import io.github.xechoz.DrawIndicator
import io.github.xechoz.LineIndicator
import io.github.xechoz.SwipeAnimateIndicator
import io.github.xechoz.boring.databinding.TabPageBinding
import io.github.xechoz.boring.databinding.TabViewBinding

class MainActivity : ComponentActivity() {
    private val tabsData = listOf("Foo", "HelloWord", "Android", "Kotlin").map { TabData(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // tab width wrap_content demo
        initTabLayout(
            tabLayout = findViewById(R.id.tab_layout),
            viewPager = findViewById(R.id.view_pager)
        )

        initViewPager(viewPager = findViewById(R.id.view_pager), tabsData)

        // tab width equals demo
        initTabLayoutWidthEquals(
            tabLayout = findViewById(R.id.tab_layout_width_equals),
            viewPager = findViewById(R.id.view_pager2)
        )

        initViewPager(viewPager = findViewById(R.id.view_pager2), tabsData)
    }

    private fun initTabLayout(tabLayout: BoringTabLayout, viewPager: ViewPager2) {
        // create tabs from data
        val tabViews = tabsData.map { tabData ->
            val tabView = FooTabView(this) // your custom tab view
            // set layout params as your need
            tabView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            tabView.setData(tabData)

            return@map tabView
        }

        // add to layout
        tabLayout.setTabs(viewPager = viewPager, tabs = tabViews)

        // custom indicator
        tabLayout.drawIndicator = SwipeAnimateIndicator(
            tabViews, LineIndicator(
                tabViews, 100, 20, Color.GREEN, 16f,
                marginBottom = 8f
            )
        )
    }

    private fun initTabLayoutWidthEquals(tabLayout: BoringTabLayout, viewPager: ViewPager2) {
        // create tabs from data
        val tabViews = tabsData.map { tabData ->
            val tabView = FooTabView(this) // your custom tab view
            // set layout params as your need
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            tabView.setLayoutParams(params)
            tabView.setData(tabData)

            return@map tabView
        }

        // add to layout
        tabLayout.setTabs(viewPager = viewPager, tabs = tabViews)

        // custom indicator
//        tabLayout.drawIndicator = SwipeAnimateIndicator(
//            tabViews, DotIndicator(
//                tabViews
//            )
//        )

//        tabLayout.drawIndicator = SwipeAnimateIndicator(
//            tabViews, DotIndicator(
//                tabViews
//            )
//        )

        tabLayout.drawIndicator = SwipeAnimateIndicator(
            tabViews, DrawableIndicator(
                tabs = tabViews,
                icon = resources.getDrawable(R.drawable.ic_launcher_foreground),
                width = 100,
                height = 100,
                marginBottom = -24
            )
        )
    }
}

private fun MainActivity.initViewPager(viewPager: ViewPager2, tabs: List<TabData>) {
    // your page adapter
    viewPager.adapter = object : RecyclerView.Adapter<FooPage>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooPage {
            return FooPage(parent)
        }

        override fun getItemCount() = tabs.size

        override fun onBindViewHolder(holder: FooPage, position: Int) {
            holder.onBindViewHolder(tabs[position])
        }
    }
}

private data class TabData(val title: String)

private class FooTabView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding = TabViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(tabData: TabData) {
        binding.title.text = tabData.title
        isSelected = false // default style
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)

        // on tab selected changed
        binding.title.typeface = if (selected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.tabRoot.setBackgroundColor(if (selected) Color.YELLOW else Color.WHITE)

        // change width
    }
}

private class FooPage(
    parent: ViewGroup, private val binding: TabPageBinding = TabPageBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun onBindViewHolder(tabData: TabData) {
        binding.content.text = "Page ${tabData.title}"
    }
}

// demo indicator
private fun DotIndicator(
    tabs: List<View>
): DrawIndicator {
    val width = 24f
    val height = 24f
    val space = 8f

    val rect = Rect()
    val paint = Paint()
    paint.style = Paint.Style.FILL
    paint.color = Color.YELLOW

    return fun(
        canvas: Canvas,
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        val tab = tabs[position]

        // center, bottom of tab view
        canvas.drawRoundRect(
            tab.centerX - width/2f,
            canvas.height - height - space,
            tab.centerX + width/2f,
            canvas.height - space ,
            width, width, paint)
    }
}

private fun DrawableIndicator(tabs: List<View>,
                            icon: Drawable,
                              width: Int,
                              height: Int,
                              marginBottom: Int,
): DrawIndicator {
    return fun(
        canvas: Canvas,
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        val tab = tabs[position]
        icon.setBounds((tab.centerX - width/2).toInt(),
            canvas.height - height - marginBottom,
            (tab.centerX + width/2).toInt(),
            canvas.height - marginBottom)
        // center, bottom of tab view
        icon.draw(canvas)
    }
}

private val View.centerX: Float
    get() = left + measuredWidth / 2f