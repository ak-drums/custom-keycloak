<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('mobile_number'); section>
    <#if section = "header">
        ${msg("verifyMobileTitle")}
    <#elseif section = "form">
			<h2>${msg("verifyMobileHello",(username!''))}</h2>
			<p>${msg("verifyMobileText")}</p>
			<form id="kc-mobile-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
				<div class="${properties.kcFormGroupClass!}">
					<div class="${properties.kcLabelWrapperClass!}">
						<label for="mobile_number"class="${properties.kcLabelClass!}">${msg("verifyMobileFieldLabel")}</label>
					</div>
					<div class="${properties.kcInputWrapperClass!}">
						<input type="tel" id="mobile_number" name="mobile_number" class="${properties.kcInputClass!}"
									 value="${mobile_number}" required aria-invalid="<#if messagesPerField.existsError('mobile_number')>true</#if>"/>
              <#if messagesPerField.existsError('mobile_number')>
								<span id="input-error-mobile-number" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('mobile_number'))?no_esc}
								</span>
              </#if>
					</div>
				</div>
				<div class="${properties.kcFormGroupClass!}">
					<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
						<input id = "verify-button" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmit")}"/>
					</div>
				</div>
			</form>

			<form id="kc-mobile-update-form-resend" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">

            	<div class="${properties.kcFormGroupClass!}">
            			<div id="kc-form-buttons-2" class="${properties.kcFormButtonsClass!}">
            			            <input type="hidden" name="resendSms" value="true">
            						<input id="resend-button" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("resend")}"/>
            			</div>
            	</div>
           </form>

           <script>
                var myvariable = "${username}";
                var expInSeconds = "${expInSeconds!0}";
                var exceededMaxAttempts = !!${exceededMaxAttempts?string("true", "false")}
                console.log("Received username " + expInSeconds);

                if (expInSeconds !== null && expInSeconds > 0) {
                    console.log("Received username " + expInSeconds);

                    document.getElementById('resend-button').disabled = true;
                    if (exceededMaxAttempts) {
                        document.getElementById('verify-button').disabled = true;
                    } else {
                        document.getElementById('verify-button').disabled = false;
                    }
                    setTimeout(function(){
                        document.getElementById('resend-button').disabled = false;
                        document.getElementById('verify-button').disabled = true;
                        },expInSeconds * 1000);
                } else {
                    document.getElementById('verify-button').disabled = true;
                    document.getElementById('resend-button').disabled = false;
                }
           </script>
    </#if>



</@layout.registrationLayout>