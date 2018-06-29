package app.vineshbuilds.actiononattention

import io.reactivex.disposables.CompositeDisposable
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AppTest {
    @get:Rule
    val rxRule = RxRule()

    /**Example (divided into 4 scenarios):

    1. Let's say items 1, 2 and 3 are visible in RV for 3 seconds.

    > 1, 2 and 3 are tracked once the first 300ms is elapsed

    2. Now user scrolls the list, making items 3, 4 and 5 visible for 300ms, consequently making 1 and 2 items go off-screen for that much time.

    > Items 4 and 5 are tracked. Tracking for item 3 is skipped as it is not a new item.

    3. User scrolls to the end of list, making items 6, 7 and 8 visible for less than 300ms, then scrolls back to the top, making the initial 1, 2 and 3 items visible again.

    > Tracking for items 6, 7 and 8 are skipped

    4. User keeps 1, 2 and 3 visible for a second.

    > 1, 2 and 3 are tracked once the first 300ms is elapsed
     */

    @Test
    fun test_Tracking() {
        val disposable = CompositeDisposable()
        val tracker = Tracker()
        //case 1
        val oCase1 = tracker.trackIfNotPreviouslyTracked(1, 3).test().assertEmpty()
        with(oCase1) {
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
            rxRule.advanceTimeBy(101, TimeUnit.MILLISECONDS)
            assertResult(mutableListOf(1, 2, 3))
        }
        disposable.addAll(oCase1)

        //case 2
        val oCase2 = tracker.trackIfNotPreviouslyTracked(3, 3).test().assertEmpty()
        with(oCase2) {
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
            rxRule.advanceTimeBy(101, TimeUnit.MILLISECONDS)
            assertResult(mutableListOf(4, 5))
        }
        disposable.addAll(oCase2)


        //case 3
        val oCase3 = tracker.trackIfNotPreviouslyTracked(6, 3).test().assertEmpty()
        with(oCase3) {
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
        }
        disposable.addAll(oCase3)

        //case 4
        val oCase4 = tracker.trackIfNotPreviouslyTracked(1, 3).test().assertEmpty()
        with(oCase4) {
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
            rxRule.advanceTimeBy(100, TimeUnit.MILLISECONDS)
            assertEmpty()
            rxRule.advanceTimeBy(101, TimeUnit.MILLISECONDS)
            assertResult(mutableListOf(1, 2, 3))
        }
        disposable.addAll(oCase4)


        disposable.clear()
    }
}
