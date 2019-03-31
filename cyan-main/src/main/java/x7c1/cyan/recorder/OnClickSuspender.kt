package x7c1.cyan.recorder

import android.util.Log
import android.view.View
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface OnClickSuspender : View.OnClickListener {

    override fun onClick(v: View?) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                onClickView(v)
            } catch (e: Exception) {
                makeText(v?.context, "${e::class} ${e.message}", LENGTH_LONG).show()
                Log.e(this::class.java.simpleName, "[unexpected]", e)
            }
        }
    }

    suspend fun onClickView(v: View?)
}
