package com.picbank.authservice.constants;

import lombok.experimental.UtilityClass;

/**
 * Defines message keys used throughout the application for i18n.
 */
@UtilityClass
public final class MessageConstants {

    public static final String AUTH_SUCCESS_TOKEN = "auth.success.token";
    public static final String AUTH_LOGIN_START = "auth.login.start";
    public static final String AUTH_ERROR_COGNITO = "auth.error.cognito";
    public static final String AUTH_ERROR_INTERNAL = "auth.error.internal";
    public static final String AUTH_ERROR_UNEXPECTED = "auth.error.unexpected";
    public static final String AUTH_REGISTER_START = "auth.register.start";
    public static final String AUTH_REGISTER_SUCCESS = "auth.register.success";
    public static final String AUTH_REGISTER_FAILURE = "auth.register.failure";
    public static final String AUTH_ERROR_VALIDATION = "auth.error.validation";
    public static final String AUTH_ADD_USER_GROUP_SUCCESS = "auth.add.user.group.success";

    public static final String COGNITO_HASH_START = "cognito.hash.start";
    public static final String COGNITO_HASH_INIT = "cognito.hash.init";
    public static final String COGNITO_HASH_UPDATE = "cognito.hash.update";
    public static final String COGNITO_HASH_FINALIZE = "cognito.hash.finalize";
    public static final String COGNITO_HASH_SUCCESS = "cognito.hash.success";
    public static final String COGNITO_HASH_ERROR = "cognito.hash.error";

    public static final String EMAIL_SENT_SUCCESS = "email.sent.success";
    public static final String EMAIL_SENT_FAILURE = "email.sent.failure";
    public static final String EMAIL_SUBJECT_USER_ACCOUNT_READY = "email.subject.user.account.ready";
    public static final String EMAIL_BODY_USER_ACCOUNT_READY = "email.body.user.account.ready";

    public static final String ERROR_VALIDATION = "error.validation";
    public static final String ERROR_INTERNAL = "error.internal";

    public static final String SQS_SEND_START = "sqs.send.start";
    public static final String SQS_SEND_SUCCESS = "sqs.send.success";
    public static final String SQS_SEND_ERROR = "sqs.send.error";

    public static final String WORKER_SQS_CHECKING = "worker.sqs.checking";
    public static final String WORKER_SQS_RETRIEVED = "worker.sqs.retrieved";
    public static final String WORKER_SQS_PROCESSING = "worker.sqs.processing";
    public static final String WORKER_SQS_PROCESSED_SUCCESS = "worker.sqs.processed.success";
    public static final String WORKER_SQS_INVALID_MESSAGE = "worker.sqs.invalid.message";
    public static final String WORKER_SQS_INVALID_FIELDS = "worker.sqs.invalid.fields";
    public static final String WORKER_SQS_INVALID_GROUP = "worker.sqs.invalid.group";
    public static final String WORKER_SQS_DELETED = "worker.sqs.deleted";
    public static final String WORKER_SQS_SENT_DLQ = "worker.sqs.sent.dlq";
    public static final String WORKER_SQS_ERROR_CONSUMING = "worker.sqs.error.consuming";
    public static final String WORKER_SQS_ERROR_PROCESSING = "worker.sqs.error.processing";
    public static final String WORKER_SQS_ERROR_DLQ = "worker.sqs.error.dlq";
}
