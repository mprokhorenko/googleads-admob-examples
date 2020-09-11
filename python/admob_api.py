from googleapiclient import discovery
from googleapiclient.http import build_http
from oauth2client import tools
from oauth2client.client import OAuth2WebServerFlow
from oauth2client.file import Storage


class AdMobAPI:

    def __init__(self, client_id, client_secret):
        scope = 'https://www.googleapis.com/auth/admob.report'
        name = 'admob'
        version = 'v1'

        flow = OAuth2WebServerFlow(client_id=client_id,
                                   client_secret=client_secret,
                                   scope=scope)
        storage = Storage(name + '.dat')
        credentials = storage.get()
        if credentials is None or credentials.invalid:
            credentials = tools.run_flow(flow, storage)
        http = credentials.authorize(http=build_http())
        self.admob = discovery.build(name, version, http=http)

    def accounts(self):
        return self.admob.accounts().list().execute()

    def account(self, publisher_id):
        return self.admob.accounts().get(name=self.__accounts_path(publisher_id)).execute()

    def network_report(self, publisher_id, report_spec):
        request = {'reportSpec': report_spec}
        return self.admob.accounts().networkReport().generate(
            parent=self.__accounts_path(publisher_id),
            body=request).execute()

    def mediation_report(self, publisher_id, report_spec):
        request = {'reportSpec': report_spec}
        return self.admob.accounts().mediationReport().generate(
            parent=self.__accounts_path(publisher_id),
            body=request).execute()

    @staticmethod
    def __accounts_path(publisher_id):
        return f"accounts/{publisher_id}"
