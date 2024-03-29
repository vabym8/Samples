package com.App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.App.R;
import com.App.util.IabHelper;
import com.App.util.IabResult;
import com.App.util.Inventory;
import com.App.util.Purchase;

public class PurchaseTestActivity extends AppCompatActivity {
    String TAG = "InAppBilling";
    IabHelper mHelper;
    String ITEM_SKU_1 = "test_purchase_1";
    String ITEM_SKU_2 = "test_purchase_2";
    String ITEM_SKU_3 = "test_purchase_3";
    String base64EncodeedPublicKey = "myKeyFromGooglePlayConsole";
    Button one, two, three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_test);

        one = (Button) findViewById(R.id.btnOne);
        two = (Button) findViewById(R.id.btnTwo);
        three = (Button) findViewById(R.id.btnThree);

        mHelper = new IabHelper(this, base64EncodeedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "onIabSetupFinished: In App Billing Setup Failed");
                } else {
                    Log.d(TAG, "onIabSetupFinished: In App Billing Setup Success");
                }
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.launchPurchaseFlow(PurchaseShowCaseActivity.this, ITEM_SKU_1, 10001, mPurchaseFinishedListener, "mypurchasetoken1");
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.launchPurchaseFlow(PurchaseShowCaseActivity.this, ITEM_SKU_2, 10001, mPurchaseFinishedListener, "mypurchasetoken2");
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.launchPurchaseFlow(PurchaseShowCaseActivity.this, ITEM_SKU_3, 10001, mPurchaseFinishedListener, "mypurchasetoken3");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            if (result.isFailure()) {
                Log.d(TAG, "onIabPurchaseFinished: isFailure");
                return;
            } else if (info.getSku().equals(ITEM_SKU_1)) {
                consumeItem();
            } else if (info.getSku().equals(ITEM_SKU_2)) {
                consumeItem();
            } else if (info.getSku().equals(ITEM_SKU_3)) {
                consumeItem();
            }
        }

        private void consumeItem() {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        }
    };
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                Log.d(TAG, "onQueryInventoryFinished: isFailure");
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU_1), mConsumeFinishedListener);
            }
        }
    };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                Toast.makeText(getApplicationContext(), "Purchase Complete!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
