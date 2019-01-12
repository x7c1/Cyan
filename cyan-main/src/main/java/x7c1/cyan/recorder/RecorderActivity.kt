package x7c1.cyan.recorder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recorder_screen.jobcan_code
import kotlinx.android.synthetic.main.recorder_screen.recorder_log
import kotlinx.android.synthetic.main.recorder_screen.recorder_log_container
import kotlinx.android.synthetic.main.recorder_screen.submit_button
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.withContext
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EActivity
import x7c1.cyan.R
import x7c1.cyan.repository.GlobalSettings


@EActivity
class RecorderActivity : AppCompatActivity() {

    @Bean
    lateinit var settings: GlobalSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recorder_screen)

        submit_button.setOnClickListener(OnSubmitClicked(
            provider = JobcanContextImpl(window.decorView),
            settings = settings,
            console = ConsoleImpl(window.decorView)
        ))
        settings.getCurrentCode()?.let {
            jobcan_code.setText(it)
        }
    }

}

private class JobcanContextImpl(
    override val containerView: View) : JobcanCodeProvider, LayoutContainer {

    override suspend fun loadCurrentCode(): String? {
        return withContext(UI) {
            jobcan_code.text.toString()
        }
    }
}

private class ConsoleImpl(
    override val containerView: View) : Console, LayoutContainer {

    override suspend fun info(message: String) {
        withContext(UI) {
            // todo: use RecyclerView
            recorder_log.text = "${recorder_log.text}[${current()}] $message\n"
            recorder_log_container.post {
                recorder_log_container.fullScroll(View.FOCUS_DOWN)
            }
            Log.i("ConsoleImpl", message)
        }
    }

    override suspend fun error(message: String) {
        info(message)
    }
}
