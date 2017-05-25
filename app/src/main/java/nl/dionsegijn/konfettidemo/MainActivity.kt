package nl.dionsegijn.konfettidemo

import android.animation.Animator
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.interfaces.OnConfigurationChangedListener
import nl.dionsegijn.konfettidemo.interfaces.OnSimpleTabSelectedListener
import nl.dionsegijn.konfettidemo.interfaces.SimpleAnimatorListener

/**
 * Created by dionsegijn on 3/25/17.
 */
class MainActivity : AppCompatActivity(), OnConfigurationChangedListener {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupTabSelectionBottomSheetBehavior()
        viewConfigurationControls.setOnConfigurationChangedListener(this)
        bottomSheetBehavior = BottomSheetBehavior.from(viewConfigurationControls)

        viewKonfetti.setOnClickListener {
            startConfetti()
        }
    }

    /**
     * Implement expand and collapse behavior for the BottomSheet used to display the configuration
     * options.
     * - Reselect a tab and the bottom sheet will either collapse or expand depending on its current
     * state.
     * - Select a tab that wasn't active yet and the BottomSheet will expand
     */
    fun setupTabSelectionBottomSheetBehavior() {
        viewConfigurationControls.setOnTabSelectedListener(object : OnSimpleTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state =
                        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            BottomSheetBehavior.STATE_EXPANDED
                        } else {
                            BottomSheetBehavior.STATE_COLLAPSED
                        }
            }
        })
    }

    fun startConfetti() {
        val config = viewConfigurationControls.configuration.active
        val selectedColors = config.colors.map { color(it) }.toIntArray()
        when (config.type) {
            Configuration.TYPE_STREAM_FROM_TOP -> streamFromTop(config, selectedColors)
            Configuration.TYPE_DRAG_AND_SHOOT -> { }
            Configuration.TYPE_BURST_FROM_CENTER -> { burstFromCenter(config, selectedColors) }
        }
    }

    fun streamFromTop(config: Configuration, colors: IntArray) {
        viewKonfetti.build()
                .addColors(*colors)
                .setDirection(0.0, 359.0)
                .setSpeed(config.minSpeed, config.maxSpeed)
                .setFadeOutEnabled(true)
                .setTimeToLive(config.timeToLive)
                .addShapes(*config.shapes)
                .addSizes(Size.SMALL)
                .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                .stream(300, 5000L)
    }

    fun burstFromCenter(config: Configuration, colors: IntArray) {
        viewKonfetti.build()
                .addColors(*colors)
                .setDirection(0.0, 359.0)
                .setSpeed(config.minSpeed, config.maxSpeed)
                .setFadeOutEnabled(true)
                .setTimeToLive(config.timeToLive)
                .addShapes(*config.shapes)
                .addSizes(Size.SMALL)
                .setPosition(viewKonfetti.x + viewKonfetti.width / 2, viewKonfetti.y + viewKonfetti.height / 3)
                .burst(100)
    }

    fun color(resId: Int): Int {
        return ContextCompat.getColor(applicationContext, resId)
    }

    override fun onConfigurationChanged(selected: Configuration) {
        textViewInstructions.animate().alpha(0f).setDuration(300L).setListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animation: Animator?) {
                textViewInstructions.setText(selected.instructions)
                textViewInstructions.animate().alpha(1f).setDuration(300L).setListener(null)
            }
        })
    }
}
