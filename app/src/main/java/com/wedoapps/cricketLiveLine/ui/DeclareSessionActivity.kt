package com.wedoapps.cricketLiveLine.ui

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.base.BaseActivity
import com.wedoapps.cricketLiveLine.databinding.ActivityDeclareSessionSodaBinding
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionBet
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionData
import com.wedoapps.cricketLiveLine.model.sessionBet.declaresession.AccountModel
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.utils.*
import cricket.t20.api.RetrofitService
import java.text.DecimalFormat
import java.util.*

class DeclareSessionActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityDeclareSessionSodaBinding
    private lateinit var viewModel: CricketGuruViewModel

    private var dialog: Dialog? = null
    private var isUpdated = false
    private var mDialog: AlertDialog? = null
    private var dbHelper: CricketGuruDatabase? = null
    private var sessionModel: SessionBet? = null
    private var sessionBet = SessionBet()
    private var sessionSodaModelArrayList: ArrayList<SessionData>? = null
    private val indianCurrencyFormat = DecimalFormat("##,##,###")
    private var finalAmount: Double? = null
    private var sessionId = ""
    private var Id = ""
    private val fireStore = FirebaseFirestore.getInstance()

    //private val documentReferenceShowMessageAds = fireStore.document(showAdMessageView)
    // private val documentReferenceShowMessageNewAds = fireStore.document(AppConstants.getNewAds())
    private var animBlink: Animation? = null


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setSessionData()
        //mBinding = ActivityDeclareSessionSodaBinding.inflate(layoutInflater)

        //setContentView(mBinding.root)
        //initView()
    }

    override fun getResource(): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        window!!.setFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )
        window!!.setDimAmount(0.6F)

        return R.layout.activity_declare_session_soda
    }


    override fun initView() {

        mBinding = getBinding()
        this.setFinishOnTouchOutside(false)
        window.setBackgroundDrawableResource(R.drawable.rounded_shape_corner_app)
        val dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT
        val dialogWidth = (resources.displayMetrics.widthPixels * 0.90)
        window.setLayout(dialogWidth.toInt(), dialogHeight)

        animBlink = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.blink
        )
        //mBinding.txtAddMsgDeclareSession.startAnimation(animBlink)
        val retrofitService = RetrofitService.getInstance()

        val repository = CricketGuruRepository(application, CricketGuruDatabase(this),retrofitService)
        val viewModelProvider = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProvider)[CricketGuruViewModel::class.java]

        setSessionData()
        //handleListener()
    }

    override fun handleListener() {
        mBinding.btnCancelDeclareSession.setOnClickListener(this)
        mBinding.btnDeclareSession.setOnClickListener(this)
        mBinding.inputEdtAmountDeclareSession.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s === "" || TextUtils.isEmpty(s)) {
                    mBinding.txtFinalAmountDeclareSession.text = ""
                } else {
                    finalAmount = calculateAmount(Integer.valueOf(s.toString()))
                    if (finalAmount!! >= 0) {
                        mBinding.txtFinalAmountDeclareSession.setTextColor(
                            resources.getColor(
                                R.color.blue,
                                theme
                            )
                        )
                    } else {
                        mBinding.txtFinalAmountDeclareSession.setTextColor(
                            resources.getColor(
                                R.color.colorRed,
                                theme
                            )
                        )
                    }
                    mBinding.txtFinalAmountDeclareSession.text = String.format(
                        "%s = %s",
                        Constants.KBOOKNAME,
                        indianCurrencyFormat.format(finalAmount)
                    )
                    ShowLogToast.showLog("Final Amount$finalAmount")
                }
            }

            override fun afterTextChanged(s: Editable) {}

        })

    }

    override fun displayMessage(message: String) {
        mBinding.root.snack(message)
    }

    /*  fun displayMessage(message: String) {
         root.snack(message)
     }*/

    override fun initProgressBar() {
        dialog = Dialog(this@DeclareSessionActivity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(false)
    }

    override fun showLoadingIndicator(isShow: Boolean) {
        dialog!!.isVisible(isShow, dialog)
    }


    private fun setSessionData() {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        dbHelper = CricketGuruDatabase(this@DeclareSessionActivity)
        val intentSession = intent
        Log.d("Test Intent", intentSession.getStringExtra("id").toString())
        Log.d("Test Intent sessionID", intentSession.getIntExtra("sessionID", 0).toString())
        if (intentSession != null) {
            Id = intentSession.getStringExtra("id").toString()
            sessionId = intentSession.getIntExtra("sessionID", 0).toString()
            sessionModel = intentSession.getParcelableExtra("sessionModel")
        }

        viewModel.getAllSessionsList(sessionId).observe(this) {
            arrSessionSoda = it as ArrayList<SessionBet>?
            it.forEach { data ->
                sessionBet = data
                partyModelArrayList.add(data.playerName.toString())
            }
            Log.d("Test arraySize", it.size.toString())
            allSessionSoda()
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun declareSessionSoda() {
        val sessionModel1 = sessionModel
        val run = Objects.requireNonNull(mBinding.inputEdtAmountDeclareSession.text).toString()
        if (TextUtils.isEmpty(run)) {
            Toast.makeText(
                this@DeclareSessionActivity,
                resources.getString(R.string.alert_insert_run_session_soda),
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (finalAmount != null) {
                sessionModel1!!.sessionID = sessionModel1.sessionID
                sessionModel1.amount = finalAmount!!.toInt()
                sessionModel1.actualScore = Integer.valueOf(run)
                val dbUpdate = viewModel.updateSessionBet(sessionModel1)
                if (dbUpdate.isCompleted) {
                    isUpdated = true
                }
            }
            if (isUpdated) {
                insertRecordAccount(run)
                showAlertDialogSessionSodaDeclare(resources.getString(R.string.insert_session_declare))
            } else {
                finish()
                Toast.makeText(
                    this@DeclareSessionActivity,
                    "Error While Insert Record",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun calculateAmount(runScore: Int): Double {
        // var finalAmount:Double = 0.0
        // DecimalFormat form = new DecimalFormat("0.000");
        var calAmount = 0.0
        if (arrSessionSoda != null && arrSessionSoda!!.size > 0) {
            for (i in arrSessionSoda!!.indices) {
                val sodaModel = arrSessionSoda!![i]
                //let objSessionSoda = arrSessionSoda[i] as !SessionSoda
                if (sessionModel!!.commission!!.toInt() == 0) {
                    sodaModel.commission = 0.0
                }

                //Fandp= bhav
                calAmount = if (sodaModel.YorN == 1) {
                    if (runScore >= sodaModel.actualScore!!) {
                        calAmount + sodaModel.FandP!! * sodaModel.amount!! + sodaModel.amount!! * sodaModel.commission!! / 100
                    } else {
                        calAmount + sodaModel.amount!! * sodaModel.commission!! / 100 - sodaModel.amount!!
                    }
                } else {
                    if (runScore >= sodaModel.actualScore!!.toInt()) {
                        calAmount + sodaModel.amount!! * sodaModel.commission!! / 100 - sodaModel.amount!! * sodaModel.FandP!!
                    } else {
                        calAmount + sodaModel.amount!! * sodaModel.commission!! / 100 + sodaModel.amount!!
                    }
                }
            }
        }
        ShowLogToast.showLog("Final value of amount calculate=====>$calAmount")
        return calAmount
    }

    /*fun calculateAmount(runScore: Int): Double {
        // var finalAmount:Double = 0.0
        // DecimalFormat form = new DecimalFormat("0.000");
        var calAmount = 0.0
        if (arrSessionSoda != null && arrSessionSoda!!.size > 0) {
            for (i in arrSessionSoda!!.indices) {
                val sodaModel = arrSessionSoda!![i]
                //let objSessionSoda = arrSessionSoda[i] as !SessionSoda
                if (sessionModel!!.YorN == 0) {
                    sodaModel.commission = 0.0
                }
                calAmount = if (sodaModel.YorN == 1) {
                    if (runScore >= sodaModel.actualScore!!) {
                        calAmount + sodaModel.rate!! * sodaModel.amount!! + sodaModel.amount!! * sodaModel.commission!! / 100
                    } else {
                        calAmount + sodaModel.amount!! * sodaModel.commission!! / 100 - sodaModel.amount!!
                    }
                } else {
                    if (runScore >= sodaModel.actualScore!!) {
                        calAmount + sodaModel.amount!! * sodaModel.commission!! / 100 - sodaModel.amount!! * sodaModel.rate!!
                    } else {
                        calAmount + sodaModel.amount!! * sodaModel.commission!! / 100 + sodaModel.amount!!
                    }
                }
            }
        }
        ShowLogToast.showLog("Final value of amount calculate=====>$calAmount")
        return calAmount
    }*/

    private fun allSessionSoda() {
        sessionSodaModelArrayList = ArrayList()
        val arrBookieID = ArrayList<String>()
        for (i in arrSessionSoda!!.indices) {
            val objSessionSoda = arrSessionSoda!![i]
            arrBookieID.add(objSessionSoda.sessionID!!)
        }
        val integerHashSet = HashSet(arrBookieID)
        arrBookieID.clear()
        arrBookieID.addAll(integerHashSet)
        for (i in arrBookieID.indices) {
            val setVal = arrBookieID[i]
            val objSessionLocal = SessionData()
            for (j in arrSessionSoda!!.indices) {
                val objSession = arrSessionSoda!![j]
                if (setVal == objSession.sessionID!!) {
                    objSessionLocal.sessionBet.add(objSession)
                }
            }

            val arrSet = ArrayList(partyModelArrayList)
            for (j in arrSet.indices) {
                val objParty = arrSet[j]
                val sessionData = SessionData()
                for (i in 0 until arrSessionSoda!!.size) {
                    val sessionBet = arrSessionSoda!![i]
                    if (setVal == objParty) {
                        sessionData.sessionID = sessionBet.sessionID
                        sessionData.playerName = sessionBet.playerName
                        sessionData.sessionBet.add(sessionBet)
                        break
                    }
                }
            }
            sessionSodaModelArrayList!!.add(objSessionLocal)
        }
    }

    private fun insertRecordAccount(finalRun: String) {
        var amountForBook = 0.0
        var commiForBook = 0.0
        for (i in 0 until sessionSodaModelArrayList!!.size + 1) {
            if (i < sessionSodaModelArrayList!!.size) {
                val objSessionLocal = sessionSodaModelArrayList!![i]
                val accountModel = AccountModel()
                accountModel.matchID = 0
                accountModel.sessionID = sessionId
                accountModel.partyName = objSessionLocal.playerName!!
                accountModel.isMatch = 0
                var amount = 0.0
                var commiAmount = 0.0
                for (j in objSessionLocal.sessionBet.indices) {
                    val objSessionSoda = objSessionLocal.sessionBet[j]
                    if (sessionModel!!.YorN == 0) //objSession= sessionModel;
                    {
                        objSessionSoda.commission = 0.0
                    }
                    amount = if (objSessionSoda.YorN == 1) {
                        if (Integer.valueOf(finalRun) >= objSessionSoda.actualScore!!) {
                            amount + objSessionSoda.amount!! * objSessionSoda.rate!!
                        } else {
                            amount - objSessionSoda.amount!!
                        }
                    } else {
                        if (Integer.valueOf(finalRun) >= objSessionSoda.actualScore!!) {
                            amount - objSessionSoda.amount!! * objSessionSoda.rate!!
                        } else {
                            amount + objSessionSoda.amount!!
                        }
                    }
                    commiAmount += objSessionSoda.amount!! * objSessionSoda.commission!! / 100
                }
                accountModel.amount = amount * -1
                accountModel.commiAmount = commiAmount * -1
                amountForBook += accountModel.amount!!
                commiForBook += accountModel.commiAmount!!
                val isInserted = viewModel.insertAccountModel(accountModel)
                if (isInserted.isCompleted) {
//                    val partyModel = dbHelper!!.getPartyFromPartyID(objSessionLocal.playerName)
                    sessionBet.amount = sessionBet.amount!! + accountModel.amount!!.toInt()
                    sessionBet.commission = sessionBet.commission!! + accountModel.commiAmount!!

                    //  partyModel.setTotalAmount(partyModel.getTotalAmount()+ accountModel.getAmount());
                    sessionBet.totalAmount =
                        sessionBet.totalAmount!! + accountModel.amount!! + accountModel.commiAmount.also {
                            sessionBet.totalAmount = it
                        }!!
                    val dbPartyUpdate = viewModel.updateSessionBet(sessionBet)
                    if (dbPartyUpdate.isCompleted) {
                        ShowLogToast.showLog("Data Inserted table")
                    } else {
                        Toast.makeText(
                            this@DeclareSessionActivity,
                            "Error in insert record",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    ShowLogToast.showLog("Error on SodaListActivity")
                }
            } else {
                val accountModel = AccountModel()
                accountModel.matchID = 0
                accountModel.sessionID = sessionModel!!.sessionID
                accountModel.isMatch = 0
                accountModel.amount = amountForBook * -1
                accountModel.commiAmount = commiForBook * -1
                accountModel.partyName = Constants.KBOOKID.toString()
                val isInserted = viewModel.insertAccountModel(accountModel)
                if (isInserted.isCompleted) {
                    ShowLogToast.showLog("Data is inserted")
//                    val partyModel = dbHelper!!.getPartyFromPartyID(AppConstants.KBOOKID)
                    sessionBet.amount = sessionBet.amount!! + accountModel.amount!!.toInt()
                    sessionBet.commission =
                        sessionBet.commission!! + accountModel.commiAmount!!

                    //  partyModel.setTotalAmount(partyModel.getTotalAmount()+ accountModel.getAmount());
                    sessionBet.totalAmount =
                        sessionBet.totalAmount!! + accountModel.amount!! + accountModel.commiAmount.also {
                            sessionBet.totalAmount = it
                        }!!
                    val dbPartyUpdate = viewModel.updateSessionBet(sessionBet)
                    if (dbPartyUpdate.isCompleted) {
                        ShowLogToast.showLog("Data Inserted table")
                    } else {
                        Toast.makeText(
                            this@DeclareSessionActivity,
                            "Error in insert record",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    ShowLogToast.showLog("Data not inserted  problem occur")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAlertDialogSessionSodaDeclare(msg: String) {
        val builder1 = AlertDialog.Builder(this@DeclareSessionActivity)

        // Set a title for alert dialog
        builder1.setTitle(resources.getString(R.string.declare_session))

        // Show a message on alert dialog
        builder1.setMessage(msg)
        builder1.setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            goToSessionActivity()
        }

        // Create the alert dialog
        val dialog1 = builder1.create()

        // Finally, display the alert dialog
        // Objects.requireNonNull(dialog1.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimationBottomToTop;
        dialog1.setCanceledOnTouchOutside(false)
        val mDrawableBackground = ContextCompat.getDrawable(
            this@DeclareSessionActivity,
            R.drawable.rounded_shape_corner_app
        )
        dialog1.window!!.setBackgroundDrawable(mDrawableBackground)
        val dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT
        val dialogWidth = (resources.displayMetrics.widthPixels * 0.90)
        dialog1.window!!.setLayout(dialogWidth.toInt(), dialogHeight)
        dialog1.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )
        dialog1.window!!.setDimAmount(0.6F)

        dialog1.show()

        // Get the alert dialog buttons reference
        val positiveButton = dialog1.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(R.color.black, theme))
        positiveButton.setBackgroundColor(resources.getColor(R.color.blue, theme))
    }

    private fun goToSessionActivity() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        if (mDialog != null) {
            mDialog!!.dismiss()
            mDialog = null
        }
        super.onDestroy()
    }

    companion object {
        //        private const val TAG = "DeclareSessionActivity"
        var arrSessionSoda: ArrayList<SessionBet>? = null
        var partyModelArrayList = hashSetOf<String>()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCancelDeclareSession -> {
                finish()
            }

            R.id.btnDeclareSession -> {
                declareSessionSoda()
            }
        }
    }
}
