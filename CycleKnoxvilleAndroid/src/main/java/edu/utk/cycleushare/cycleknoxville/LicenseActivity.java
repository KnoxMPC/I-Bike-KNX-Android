/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.utk.cycleushare.cycleknoxville;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Displays the open source license info for Google Play services.
 */
public class LicenseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        ScrollView scroll = new ScrollView(this);
        TextView license = new TextView(this);
        String ourLicense = "I Bike KNX is an app and affiliated webmap that was developed for a research project aimed at improving bicycle safety. This project is funded by Southeastern Transportation Center. The data, presented in aggregate form, will be used by the research team of the project, and results will be shared with Knoxville Transportation Planning Organization, bicycle program planners and the City of Knoxville Engineering Department to assist in bicycle planning and design, so that the agencies can more effectively prioritize cycling infrastructure investments. These terms of use and privacy policy explain the services that are provided by the IBK Team and I Bike KNX, the terms under which those services are being provided, and describe how personal information is collected, used, and disclosed when you use I Bike KNX through your mobile device. These terms and the privacy policy apply to all users of I Bike KNX, and by using I Bike KNX, you are accepting and agreeing to these terms. If you do not agree to these terms of use and privacy policy, do not use I Bike KNX. The IBK Team may revise these terms of use and privacy policy at any time, and will notify you of any changes in by posting the revised information here. Any questions concerning these terms of use and privacy policy should be directed to cherry@utk.edu. \n" +
        "\n" +
                "TERMS OF USE\n" +
                "\n" +
                "I Bike KNX offers you the ability to track and record your bicycle trip routes, times, and purposes, and to view the bicycle trip routes of others, while also allowing local transportation planners to study and base decisions on users’ actual bicycle usage. Use of I Bike KNX requires your mobile device to have and use a data connection; your carrier’s normal rates and fees will apply. \n" +
                "\n" +
                "User conduct and content. When you use I Bike KNX, information including your longitude/latitude, speed, and velocity will be collected. To use some of the optional web-based features of I Bike KNX, you can input certain additional personal information and will have the option to create a personal account. If you input additional personal information, you must provide true and accurate information. Your failure to do so may negatively impact your ability to use the features of I Bike KNX. If you create an account, you are responsible for any actions that take place under your account. Keep your username and password secure and do not allow anyone else to use them. By submitting content to the I Bike KNX app (whether by inputting information such as your name, email address, age, etc., or by allowing the app to collect automatically generated information such as your GPS location), you grant the IBK Team a non-exclusive, non-revocable, worldwide, transferable, royalty-free, perpetual right to use that information in any manner or media now or later developed, for any purpose, commercial, advertising, or otherwise, including the right to translate, display, reproduce, modify, create derivative works, sublicense, distribute, assign, and commercialize without any payment due to you; provided, however, that such use shall be in accordance with the Privacy Policy, below. The IBK Team does not monitor or prescreen user-generated content before it is posted and does not undertake any obligation or liability related to any such content, but reserves the right to remove or edit any such content or to terminate your account for any reason. If you elect to create a I Bike KNX account using a social networking account (such as a Facebook, Google, or other social networking account), you acknowledge and agree that information that you submit to and that is collected by I Bike KNX may be shared via that social networking account. I Bike KNX does not monitor or prescreen content before it is posted to your social networking account; you are solely and personally responsible for any content that may be posted to your social networking accounts.\n" +
                "\n" +
                "Age restrictions. I Bike KNX is intended solely for use by users who are 13 years of age or older, and it is a violation of these terms of use for anyone under the age of 13 to register. By downloading and using I Bike KNX, you represent and warrant that you are 13 years or older. If you are 13 or older, but under the age of 18, you must review these terms with your parent(s) or legal guardian(s) to ensure that both you and your parent(s) or legal guardian(s) understand and consent to the terms. A parent or legal guardian accepting these terms for the benefit of a child agrees and accepts full responsibility for the child’s use of I Bike KNX, including all financial charges and legal liability that the child may incur. \n" +
                "\n" +
                "Personal safety notice. You understand that all your athletic and/or recreational activities, including bicycling while using I Bike KNX, may have inherent, implicit, and/or express risks of bodily injury or death and/or property damage. You understand and agree that you voluntarily and at your own free will assume all known and unknown risks associated with such athletic or recreational activities, even if such risks may be claimed to be caused in whole or in part by the actions, inactions, or negligence of the IBK Team or others. You understand that the IBK Team will not carry out and is not responsible for any physical inspection, supervision, or preparation of any of the routes displayed in the I Bike KNX app and webmap, and that the inclusion of a route is in no way an endorsement or recommendation of any roadway, path, or other route for bicycling, nor are they an indication that any roadway, path, or other route is intended for use by bicyclists. Potential hazards and obstructions may exist on the routes shown, and the IBK Team and its partner agencies in no way warrant the safety or fitness of the routes shown. The IBK Team encourages you to always put safety first, follow applicable traffic regulations, do not change settings on your mobile device or I Bike KNX while in motion or in unsafe areas, and always exercise due diligence when exercising. \n" +
                "\n" +
                "I Bike KNX" +
                "\n" +
                "Limitation of liability. To the fullest extent of the law, the IBK Team shall not jointly or severally be liable for any direct, special, incidental, indirect, or consequential damages, including but not limited to lost profits, business interruptions, or lost data that result from the use of or the inability to use I Bike KNX and any user generated content, even if the IBK Team has been advised of the possibility of such damages. You expressly agree and promise not to sue any member of the IBK Team for any claims, injuries, damages, or losses associated with your use of I Bike KNX. Applicable law may not allow the limitation or exclusion of liability or incidental or consequential damages, so the foregoing limitation may not apply to you in its entirety. However, you agree that the IBK Team’s liability will be limited to the maximum extent of the law. If links are established to a third-party website, the IBK Parties are not liable for the contents of that website. \n" +
                "\n" +
                "Indemnity. You agree to indemnify, defend, and hold harmless the IBK Team from any and all claims, losses, liabilities, expenses, damages, and costs, including without limitation reasonable attorneys’ fees, arising from or relating in any way to your use of I Bike KNX and your conduct in connection with I Bike KNX, other users of I Bike KNX, or any violation of these terms. \n" +
                "\n" +
                "Termination. The IBK Team reserves the right to change, discontinue, and/or terminate I Bike KNX at any time without notice, at its own discretion, and may edit or remove in whole or in part any user’s account or user-generated content. You understand and agree that some of your user generated content may continue to appear on I Bike KNX or other platforms, even after your account is terminated. \n" +
                "\n" +
                "PRIVACY POLICY\n" +
                "\n" +
                "I Bike KNX uses your mobile device’s GPS capabilities to record your bicycle trip routes, times, speed, velocity and purposes, along with other information that you may elect to enter into the app (age, email, gender, ethnicity, home income, home zip, work zip, school zip, cycling frequency, rider type, rider history, etc.). \n" +
                "\n" +
                "You consent to sharing information with the I Bike KNX community and the general public. By using I Bike KNX, you agree and acknowledge that we may disclose and make available to other I Bike KNX users or to the general public on the I Bike KNX app or on a website information about your bicycle routes. To protect your privacy, your trips will be shown anonymously and the precise start and end locations of your trips will be obscured from public view; trips will be available to the public based on route purpose. If you elect to create a I Bike KNX account using a social networking account (e.g., Facebook, Google, etc.) and allow I Bike KNX permissions to “share” information with that account, you agree and acknowledge that any information that you have inputted into the I Bike KNX app or that has been collected by the I Bike KNX app may be shared to your social networking account and will be subject to the permissions and privacy settings that you have put in place for that account. Neither I Bike KNX nor the IBK Team is responsible for monitoring content that may be pushed by I Bike KNX to your social networking account or for ensuring that your social networking account settings are appropriate. \n" +
                "\n" +
                "You consent to sharing information with transportation planning agencies. By using I Bike KNX, you agree and acknowledge that the information that is recorded by I Bike KNX may be shared among members of the IBK Team, including transportation planners, Knoxville Transportation Planning Organization, and related state agencies, for analysis and use to inform their decisions about bicycle facilities in the region and to allow them to better understand and predict where cyclists will ride and how land developments and transportation infrastructure affect cycling activity. Information transmitted to transportation planners will include only trip information (which includes GPS data, your trip route, time, speed, velocity and purposes) and your iPhone's Unique Device Identifier (UDID) or Android phone's Device ID; information transmitted to transportation planners will not include your name, age, gender, or other personal information unless you specifically authorize the disclosure of that information by entering that information into the I Bike KNX app. \n" +
                "\n" +
                "Use and transfer of information for business and legal purposes. By using I Bike KNX, you acknowledge and agree that your aggregated or otherwise de-identifiable personal information may be used by I Bike KNX for any purpose and may be shared publicly or with I Bike KNX’s business partners. Your information may be transferred from I Bike KNX if there is a business transaction or transfer of ownership of I Bike KNX; if this happens, you will be notified of the change and your personal information will continue to be protected in accordance with this Privacy Policy. You acknowledge and agree that the IBK Parties may access, preserve, and disclose your information if required to do so by law or in a good faith belief that such access or preservation or disclosure is reasonably necessary to comply with a legal process, enforce the terms of use, or protect the rights, property, or safety of I Bike KNX, its users, and the public.\n" +
                "\n" +
                "Data protection. In order to protect your personal information that is stored by I Bike KNX, we and our data center service providers have implemented various security measures to keep your personal information stored in a secure environment and treated confidentially. Your personal information is only accessible to a limited number of I Bike KNX personnel and research group of the project, who all have special access rights and are required to keep the information confidential.\n\n";

        // prepend our license to the standard Play Services license text
        license.setText(ourLicense + GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
        scroll.addView(license);
        setContentView(scroll);
    }
}
