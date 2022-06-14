@Library('jenkins-ci-automation@master') _

BlibliPipeline ([
  type : "java",
  modules: [
    Docker: null,
    Analysis: null
  ],
  deploy_branch_regex : "(master|release/.+)",
  application : [
      tribe : "rnd",
      squad : "backend",
      service_name : "blibli-backend-framework"
    ]
])