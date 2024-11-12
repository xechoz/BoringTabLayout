package io.github.xechoz.boring

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.github.xechoz.AnimateIndicator
import io.github.xechoz.BoringTabLayout
import io.github.xechoz.LineIndicator
import io.github.xechoz.boring.databinding.TabPageBinding
import io.github.xechoz.boring.databinding.TabViewBinding

class MainActivity : ComponentActivity() {
    private val tabsData = (0..4).map { TabData("Tab $it") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        initTabLayout(
            tabLayout = findViewById(R.id.tab_layout),
            viewPager = findViewById(R.id.view_pager))

        initViewPager(viewPager = findViewById(R.id.view_pager))
    }

    private fun initTabLayout(tabLayout: BoringTabLayout, viewPager: ViewPager2) {
        // create tabs from data
       val tabViews = tabsData.map { tabData ->
            val tabView = FooTabView(this) // your custom tab view
            tabView.setData(tabData)
            // set layout params as your need
            // tabView.layoutParams = LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT)
           return@map tabView
        }

        // add to layout
        tabLayout.setTabs(viewPager = viewPager, tabs = tabViews)

        // custom indicator

        tabLayout.drawIndicator = AnimateIndicator(tabViews, LineIndicator(tabViews, 100, 20, Color.GREEN, 16f,
            marginBottom = 8f))
    }

    private fun initViewPager(viewPager: ViewPager2) {
        // your page adapter
        viewPager.adapter = object : RecyclerView.Adapter<FooPage>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooPage {
                return FooPage(parent)
            }

            override fun getItemCount() = tabsData.size

            override fun onBindViewHolder(holder: FooPage, position: Int) {
                holder.onBindViewHolder(tabsData[position])
            }
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
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)

        // on tab selected changed
        binding.title.typeface = if (selected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.tabRoot.setBackgroundColor(if (selected) Color.YELLOW else Color.WHITE)

        // change width
    }
}

private class FooPage(parent: ViewGroup, private val binding: TabPageBinding = TabPageBinding.inflate(
    LayoutInflater.from(parent.context), parent, false)): RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun onBindViewHolder(tabData: TabData) {
        binding.content.text = "Page ${tabData.title}"
    }
}