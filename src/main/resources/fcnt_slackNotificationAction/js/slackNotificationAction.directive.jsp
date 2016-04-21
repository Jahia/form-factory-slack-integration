<%@ page contentType="text/javascript" %>
<%@ taglib prefix="formfactory" uri="http://www.jahia.org/formfactory/functions" %>

        (function () {
            'use strict';

            //Define the action directive
            var slackAction = function($log, ffTemplateResolver) {
                var directive = {
                    restrict: 'E',
                    templateUrl: function(el, attrs) {
                        return   ffTemplateResolver.resolveTemplatePath(
                                '${formfactory:addFormFactoryModulePath('/form-factory-actions/slack-notification-action/', renderContext)}', attrs.viewType
                        );
                    },
                    link: linkFunc
                };
                return directive;

                function linkFunc(scope, el, attr) {
                    /**
                     * Any initialization of action properties or any other variables
                     * can be done within this function.
                     */
                }
            };
            //Attach the directive to the module
            angular
                    .module('formFactory')
                    .directive('ffSlackNotificationAction', ['$log', 'ffTemplateResolver', slackAction]);
        })();