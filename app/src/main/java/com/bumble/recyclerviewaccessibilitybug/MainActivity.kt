package com.bumble.recyclerviewaccessibilitybug

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        recyclerView.adapter = Adapter()
    }

    class CustomView(parent: ViewGroup) : View(parent.context) {
        init {
            setBackgroundColor(random.nextInt())
            minimumHeight = 100
            ViewCompat.setAccessibilityDelegate(this, Delegate())
        }

        override fun setAccessibilityDelegate(delegate: AccessibilityDelegate?) {
            super.setAccessibilityDelegate(delegate)
            val realDelegate = ViewCompat.getAccessibilityDelegate(this)
            if (realDelegate?.javaClass == AccessibilityDelegateCompat::class.java) {
                throw IllegalStateException(
                    "Delegate is being overwritten by the default one " +
                            "(neither RecyclerView ItemDelegate or our custom Delegate)"
                )
            }
        }

        companion object {
            private val random = Random(0)
        }
    }

    class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(CustomView(parent))

        override fun getItemCount(): Int = 100

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val delegate = ViewCompat.getAccessibilityDelegate(holder.itemView)
            when {
                delegate == null ->
                    throw IllegalStateException("Where is delegate?")

                delegate.javaClass == AccessibilityDelegateCompat::class.java ->
                    // The delegate was overwritten by GapWorker
                    throw IllegalStateException("Delegate was overwritten by ViewCompat.setAccessibilityDelegate")

                delegate is Delegate ->
                    Unit // OK state when the view is just created

                delegate is RecyclerViewAccessibilityDelegate.ItemDelegate ->
                    Unit // OK state when the view is recycled, ItemDelegate should still preserve the original delegate

            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }

    class Delegate : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.isSelected = true
        }
    }

}