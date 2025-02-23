package com.picbank.authservice.constants;

public interface MessageConstants {
    String AUTH_SUCCESS_TOKEN = "auth.success.token";
    String AUTH_LOGIN_START = "auth.login.start";
    String AUTH_ERROR_INVALID_CREDENTIALS = "auth.error.invalid.credentials";
    String AUTH_ERROR_COGNITO = "auth.error.cognito";
    String AUTH_ERROR_INTERNAL = "auth.error.internal";
    String AUTH_ERROR_UNEXPECTED = "auth.error.unexpected";
    String AUTH_REGISTER_START = "auth.register.start";
    String AUTH_REGISTER_SUCCESS = "auth.register.success";
    String AUTH_REGISTER_FAILURE = "auth.register.failure";
    String AUTH_ERROR_VALIDATION = "auth.error.validation";
    String AUTH_ADD_USER_GROUP_SUCCESS = "auth.add.user.group.success";

    String COGNITO_HASH_START = "cognito.hash.start";
    String COGNITO_HASH_INIT = "cognito.hash.init";
    String COGNITO_HASH_UPDATE = "cognito.hash.update";
    String COGNITO_HASH_FINALIZE = "cognito.hash.finalize";
    String COGNITO_HASH_SUCCESS = "cognito.hash.success";
    String COGNITO_HASH_ERROR = "cognito.hash.error";

    String EMAIL_SENT_SUCCESS = "email.sent.success";
    String EMAIL_SENT_FAILURE = "email.sent.failure";
    String EMAIL_SUBJECT_USER_ACCOUNT_READY = "email.subject.user.account.ready";
    String EMAIL_BODY_USER_ACCOUNT_READY = "email.body.user.account.ready";

    String ERROR_VALIDATION = "error.validation";
    String ERROR_INTERNAL = "error.internal";

    String SQS_SEND_START = "sqs.send.start";
    String SQS_SEND_SUCCESS = "sqs.send.success";
    String SQS_SEND_ERROR = "sqs.send.error";

    String WORKER_SQS_CHECKING = "worker.sqs.checking";
    String WORKER_SQS_RETRIEVED = "worker.sqs.retrieved";
    String WORKER_SQS_PROCESSING = "worker.sqs.processing";
    String WORKER_SQS_PROCESSED_SUCCESS = "worker.sqs.processed.success";
    String WORKER_SQS_INVALID_MESSAGE = "worker.sqs.invalid.message";
    String WORKER_SQS_INVALID_FIELDS = "worker.sqs.invalid.fields";
    String WORKER_SQS_INVALID_GROUP = "worker.sqs.invalid.group";
    String WORKER_SQS_DELETED = "worker.sqs.deleted";
    String WORKER_SQS_SENT_DLQ = "worker.sqs.sent.dlq";
    String WORKER_SQS_ERROR_CONSUMING = "worker.sqs.error.consuming";
    String WORKER_SQS_ERROR_PROCESSING = "worker.sqs.error.processing";
    String WORKER_SQS_ERROR_DLQ = "worker.sqs.error.dlq";
}

