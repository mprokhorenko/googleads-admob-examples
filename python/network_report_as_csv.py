import csv
import sys

from googleapiclient import discovery
from googleapiclient.http import build_http
from oauth2client import tools
from oauth2client.file import Storage
from oauth2client.client import OAuth2WebServerFlow


class AdMobAPI:

    def __init__(self):
        scope = 'https://www.googleapis.com/auth/admob.report'
        name = 'admob'
        version = 'v1'

        flow = OAuth2WebServerFlow(client_id='<todo: replace with a client_id from the secret json>',
                                   client_secret='<todo: replace with a secret from the secret json>',
                                   scope=scope)
        storage = Storage(name + '.dat')
        credentials = storage.get()
        if credentials is None or credentials.invalid:
            credentials = tools.run_flow(flow, storage)
        http = credentials.authorize(http=build_http())
        self.admob = discovery.build(name, version, http=http)

    # Convert to the list of dictionaries
    def report_to_list_of_dictionaries(self, response):
        result = []

        for report_line in response:
            if report_line.get('row'):
                print(report_line)
                row = report_line.get('row')
                dm = {}
                if row.get('dimensionValues'):
                    for key, value in row.get('dimensionValues').items():
                        if value.get('value') and value.get('displayLabel'):
                            dm.update({key: value.get('value')})
                            dm.update({key + '_NAME': value.get('displayLabel')})
                        else:
                            dm.update({key: next(filter(None, [value.get('value'), value.get('displayLabel')]))})
                if row.get('metricValues'):
                    for key, value in row.get('metricValues').items():
                        dm.update({key: next(filter(None, [value.get('value'), value.get('microsValue'), value.get('integerValue')]))})
                result.append(dm)
        return result

    def generate_report(self, publisher_id):
        date_range = {'startDate': {'year': 2020, 'month': 4, 'day': 1},
                      'endDate': {'year': 2020, 'month': 4, 'day': 1}}
        dimensions = ['DATE', 'APP', 'PLATFORM', 'COUNTRY']
        metrics = ['ESTIMATED_EARNINGS', 'IMPRESSIONS', 'CLICKS',
                   'AD_REQUESTS', 'MATCHED_REQUESTS']
        sort_conditions = {'dimension': 'DATE', 'order': 'DESCENDING'}
        report_spec = {'dateRange': date_range,
                       'dimensions': dimensions,
                       'metrics': metrics,
                       'sortConditions': [sort_conditions]}

        request = {'reportSpec': report_spec}
        return self.admob.accounts().networkReport().generate(
                parent='accounts/{}'.format(publisher_id),
                body=request).execute()

api = AdMobAPI()
raw_report = api.generate_report('<todo: replace with publisher id, smth like pub-[0-9]+>')
report_as_list_of_dictionaries = api.report_to_list_of_dictionaries(raw_report)

# Convert to CSV
dict_writer = csv.DictWriter(sys.stdout, report_as_list_of_dictionaries[0].keys())
dict_writer.writeheader()
dict_writer.writerows(report_as_list_of_dictionaries)
