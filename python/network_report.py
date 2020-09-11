import sys
from datetime import date
from datetime import timedelta

from python.admob_api import AdMobAPI
from python.report_processor import to_csv

# Create AdMob API client
client_id = '<todo: replace with a client_id from the secret json>'
client_secret = '<todo: replace with a secret from the secret json>'
api = AdMobAPI(client_id, client_secret)

# Get my account
accounts = api.accounts()

# Prepare report spec for the week
today = date.today()
week_ago = today - timedelta(days=7)
date_range = {'startDate': {'year': week_ago.year, 'month': week_ago.month, 'day': week_ago.day},
              'endDate': {'year': today.year, 'month': today.month, 'day': today.day}}
dimensions = ['DATE', 'APP', 'PLATFORM', 'COUNTRY']
metrics = ['ESTIMATED_EARNINGS', 'IMPRESSIONS', 'CLICKS', 'AD_REQUESTS', 'MATCHED_REQUESTS']
sort_conditions = {'dimension': 'DATE', 'order': 'DESCENDING'}
report_spec = {'dateRange': date_range,
               'dimensions': dimensions,
               'metrics': metrics,
               'sortConditions': [sort_conditions]}

# Generate report
for account in accounts.get('account'):
    # Account settings
    print(f"Publisher Id: {account.get('publisherId')})\n"
          f"Timezone: {account.get('publisherId')}\n"
          f"Currency: {account.get('currencyCode')}")

    # Generate report
    raw_report = api.network_report(account.get('publisherId'), report_spec)
    to_csv(raw_report, sys.stdout)
